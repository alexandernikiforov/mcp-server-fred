package ch.alni.mcp.edgar.service;

import ch.alni.mcp.edgar.core.Concept;
import ch.alni.mcp.edgar.core.FiscalYearCountType;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface EdgarService {

    /**
     * Resolves the CIK (Central Index Key) for a given ticker symbol.
     *
     * @param ticker The ticker symbol for which to resolve the CIK.
     * @return The CIK associated with the given ticker symbol.
     */
    Long resolveCik(String ticker);

    /**
     * Retrieves the reported filing history for the specified ticker and concepts.
     *
     * @param ticker          The ticker symbol of the company for which the filing history is requested.
     * @param concepts        A list of concept identifiers to retrieve filing history for.
     * @param fiscalYearCount The maximum number of fiscal years to include in the filing history.
     * @return A Flux stream of {@link ConceptFilingHistory} containing filing history data.
     */
    Mono<ConceptFilingHistoryResponse> getReportedHistory(String ticker, Set<Concept> concepts, FiscalYearCountType fiscalYearCount);
}
