package ch.alni.mcp.fred.service;

import reactor.core.publisher.Mono;

/**
 * Service for interacting with the FRED API to retrieve economic data.
 */
public interface FredService {

    /**
     * Retrieves economic data for the specified spread and window.
     *
     * @param spread the economic spread to retrieve data for
     * @param window the time window to retrieve data for
     * @return a Mono that emits the SpreadResponse containing the retrieved data
     */
    Mono<SpreadResponse> getSpread(Spread spread, Window window);
}
