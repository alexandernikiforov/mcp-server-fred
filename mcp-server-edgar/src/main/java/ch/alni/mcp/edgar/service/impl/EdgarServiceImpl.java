package ch.alni.mcp.edgar.service.impl;

import ch.alni.mcp.edgar.core.Concept;
import ch.alni.mcp.edgar.port.EdgarDataClient;
import ch.alni.mcp.edgar.service.ConceptFilingHistory;
import ch.alni.mcp.edgar.service.EdgarService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
class EdgarServiceImpl implements EdgarService {

    private final EdgarDataClient dataClient;
    private final TickerResolver tickerResolver;

    EdgarServiceImpl(EdgarDataClient dataClient, TickerResolver tickerResolver) {
        this.dataClient = dataClient;
        this.tickerResolver = tickerResolver;
    }

    @Override
    public Long resolveCik(String ticker) {
        return tickerResolver.resolve(ticker);
    }

    @Override
    public Flux<ConceptFilingHistory> getReportedHistory(String ticker, Set<Concept> concepts) {
        final Long cik = tickerResolver.resolve(ticker);
        if (cik == null) {
            return Flux.error(new IllegalArgumentException("CIK not found for " + ticker));
        }

        return dataClient.getCompanyFacts(cik)
                .flatMapMany(factsResponse -> Flux.fromIterable(concepts)
                        .concatMap(concept -> Mono.justOrEmpty(Converter.toReportedHistory(factsResponse, concept))));
    }

}
