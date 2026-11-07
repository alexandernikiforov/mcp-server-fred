package ch.alni.mcp.edgar.service.impl;

import ch.alni.mcp.edgar.core.FiscalYearCountType;
import ch.alni.mcp.edgar.port.DirectoryResponse;
import ch.alni.mcp.edgar.port.EdgarArchiveClient;
import ch.alni.mcp.edgar.port.EdgarDataClient;
import ch.alni.mcp.edgar.service.Concept;
import ch.alni.mcp.edgar.service.ConceptFilingHistoryResponse;
import ch.alni.mcp.edgar.service.EdgarService;
import ch.alni.mcp.edgar.service.FilingFactResponse;
import ch.alni.mcp.edgar.service.FilingHistory;
import ch.alni.mcp.edgar.service.FilingStructure;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

@Service
class EdgarServiceImpl implements EdgarService {

    private final EdgarDataClient dataClient;
    private final EdgarArchiveClient archiveClient;
    private final TickerResolver tickerResolver;
    private final FilingStructureResolver filingStructureResolver;
    private final FilingFactResolver filingFactResolver;

    EdgarServiceImpl(EdgarDataClient dataClient,
                     EdgarArchiveClient archiveClient,
                     TickerResolver tickerResolver,
                     FilingStructureResolver filingStructureResolver,
                     FilingFactResolver filingFactResolver) {
        this.dataClient = dataClient;
        this.archiveClient = archiveClient;
        this.tickerResolver = tickerResolver;
        this.filingStructureResolver = filingStructureResolver;
        this.filingFactResolver = filingFactResolver;
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
    public Mono<FilingHistory> getFilingHistory(String ticker, int latestSubmissions) {
        final Long cik = tickerResolver.resolve(ticker);
        if (cik == null) {
            return Mono.error(new IllegalArgumentException("CIK not found for " + ticker));
        }

        return dataClient.getCompanySubmissions(cik)
                .flatMap(response -> Flux.fromIterable(Converter.toFilings(response, latestSubmissions))
                        .collectList()
                        .map(FilingHistory::new)
                );
    }

    @Override
    public Mono<FilingStructure> getFilingStructure(String ticker, String accessionNumber) {
        final Long cik = tickerResolver.resolve(ticker);
        if (cik == null) {
            return Mono.error(new IllegalArgumentException("CIK not found for " + ticker));
        }

        final String accn = accessionNumber.replace("-", "");

        final Predicate<DirectoryResponse.Item> companyXsdPredicate = item ->
                item.name().toLowerCase(Locale.ROOT).contains(".xsd");

        final Predicate<DirectoryResponse.Item> preXmlPredicate = item ->
                item.name().toLowerCase(Locale.ROOT).contains("_pre.xml");

        return archiveClient.getDirectory(cik, accn)
                .map(DirectoryResponse::directory)
                .filter(directory -> directory.containsItem(companyXsdPredicate))
                .flatMap(directory -> archiveClient.getAccession(cik, accn, directory.getItemName(companyXsdPredicate))
                        .zipWhen(companyXsd -> directory.containsItem(preXmlPredicate)
                                        ? archiveClient.getAccession(cik, accn, directory.getItemName(preXmlPredicate))
                                        : Mono.just(companyXsd),
                                filingStructureResolver::resolve
                        ));
    }

    @Override
    public Mono<FilingFactResponse> getFilingFacts(String ticker, String accessionNumber, Set<Concept> concepts) {
        final Long cik = tickerResolver.resolve(ticker);
        if (cik == null) {
            return Mono.error(new IllegalArgumentException("CIK not found for " + ticker));
        }

        final Predicate<DirectoryResponse.Item> instancePredicate = item ->
                item.name().toLowerCase(Locale.ROOT).contains("_htm.xml");

        final String accn = accessionNumber.replace("-", "");
        return archiveClient.getDirectory(cik, accn)
                .map(DirectoryResponse::directory)
                .filter(directory -> directory.containsItem(instancePredicate))
                .flatMap(directory -> archiveClient.getAccession(cik, accn, directory.getItemName(instancePredicate)))
                .map(instance -> filingFactResolver.parseFiling(instance, concepts));
    }
}
