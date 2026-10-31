package ch.alni.mcp.edgar.service;

import ch.alni.mcp.edgar.core.Concept;
import reactor.core.publisher.Flux;

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
     * @param ticker   The ticker symbol of the company for which the filing history is requested.
     * @param concepts A list of concept identifiers to retrieve filing history for.
     * @return A Flux stream of {@link ConceptFilingHistory} containing filing history data.
     */
    Flux<ConceptFilingHistory> getReportedHistory(String ticker, Set<Concept> concepts);
}
