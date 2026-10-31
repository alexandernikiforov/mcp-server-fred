package ch.alni.mcp.edgar.service.impl;

import ch.alni.mcp.edgar.core.Concept;
import ch.alni.mcp.edgar.core.FiscalYearCountType;
import ch.alni.mcp.edgar.port.EdgarDataClient;
import ch.alni.mcp.edgar.service.ConceptFilingHistoryResponse;
import ch.alni.mcp.edgar.service.EdgarService;
import ch.alni.mcp.edgar.service.SubmissionHistory;
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
    public Mono<ConceptFilingHistoryResponse> getReportedHistory(String ticker, Set<Concept> concepts, FiscalYearCountType fiscalYearCount) {
        final Long cik = tickerResolver.resolve(ticker);
        if (cik == null) {
            return Mono.error(new IllegalArgumentException("CIK not found for " + ticker));
        }

        return dataClient.getCompanyFacts(cik)
                .flatMap(factsResponse -> Flux.fromIterable(concepts)
                        .concatMap(concept -> Mono.justOrEmpty(Converter.toReportedHistory(factsResponse, concept, fiscalYearCount.getValue())))
                        .collectList()
                        .map(ConceptFilingHistoryResponse::new)
                );
    }

    @Override
    public Mono<SubmissionHistory> getSubmissionHistory(String ticker, int latestSubmissions) {
        final Long cik = tickerResolver.resolve(ticker);
        if (cik == null) {
            return Mono.error(new IllegalArgumentException("CIK not found for " + ticker));
        }

        return dataClient.getCompanySubmissions(cik)
                .flatMap(response -> Flux.fromIterable(Converter.toSubmissions(response, latestSubmissions))
                        .collectList()
                        .map(SubmissionHistory::new)
                );
    }

}
