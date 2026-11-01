package ch.alni.mcp.edgar.service.impl;

import ch.alni.mcp.edgar.core.Concept;
import ch.alni.mcp.edgar.core.FiscalYearCountType;
import ch.alni.mcp.edgar.port.EdgarArchiveClient;
import ch.alni.mcp.edgar.port.EdgarDataClient;
import ch.alni.mcp.edgar.service.ConceptFilingHistoryResponse;
import ch.alni.mcp.edgar.service.EdgarService;
import ch.alni.mcp.edgar.service.FilingStructure;
import ch.alni.mcp.edgar.service.SubmissionHistory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
class EdgarServiceImpl implements EdgarService {

    private final EdgarDataClient dataClient;
    private final EdgarArchiveClient archiveClient;
    private final TickerResolver tickerResolver;
    private final FilingStructureResolver filingStructureResolver;

    EdgarServiceImpl(EdgarDataClient dataClient,
                     EdgarArchiveClient archiveClient,
                     TickerResolver tickerResolver,
                     FilingStructureResolver filingStructureResolver) {
        this.dataClient = dataClient;
        this.archiveClient = archiveClient;
        this.tickerResolver = tickerResolver;
        this.filingStructureResolver = filingStructureResolver;
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

    @Override
    public Mono<FilingStructure> getFilingStructure(String ticker, String accessionNumber) {
        final Long cik = tickerResolver.resolve(ticker);
        if (cik == null) {
            return Mono.error(new IllegalArgumentException("CIK not found for " + ticker));
        }

        final String accn = accessionNumber.replace("-", "");
        return archiveClient.getDirectory(cik, accn)
                .map(Converter::toPresentation)
                .flatMap(tuple -> archiveClient.getAccession(cik, accn, tuple.getT1())
                        .zipWhen(_ -> archiveClient.getAccession(cik, accn, tuple.getT2()))
                )
                .map(tuple -> filingStructureResolver.resolve(tuple.getT1(), tuple.getT2()));
    }
}
