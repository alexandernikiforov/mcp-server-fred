package ch.alni.mcp.edgar.service.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
class TickerResolver implements InitializingBean {

    private final Map<String, Long> tickersMap = new HashMap<>();
    private final JsonMapper jsonMapper;
    @Value("classpath:company_tickers.json")
    private Resource tickers;

    TickerResolver(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    long resolve(String ticker) {
        return tickersMap.get(ticker.toUpperCase());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final Map<String, CikTickerMapping> mappings = jsonMapper
                .readValue(tickers.getInputStream(), new TypeReference<>() {
                });

        final Map<String, Long> collected = mappings.values().stream()
                .collect(Collectors.toMap(CikTickerMapping::ticker, CikTickerMapping::cik));

        tickersMap.putAll(collected);
    }
}
