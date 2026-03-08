package ch.alni.mcp.edgar.service.impl;

import ch.alni.mcp.edgar.service.EdgarService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
class EdgarServiceImpl implements EdgarService {

    private final TickerResolver tickerResolver;

    EdgarServiceImpl(TickerResolver tickerResolver) {
        this.tickerResolver = tickerResolver;
    }

    @Override
    public long resolveCik(String ticker) {
        return tickerResolver.resolve(ticker);
    }
}
