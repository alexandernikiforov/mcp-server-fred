package ch.alni.mcp.fred.client;

import reactor.core.publisher.Mono;

/**
 * Connects to the FRED server.
 */
public interface FredClientService {

    /**
     * Retrieves time series observations for a specified FRED series based on the provided request parameters.
     *
     * @param request The request object specifying query parameters such as series ID, date range, frequency,
     *                transformation options, and other filter criteria for retrieving observations.
     * @return A {@code Mono} emitting the {@code ObservationsResponse} containing the requested time series
     * observations and related metadata.
     */
    Mono<ObservationsResponse> getObservations(ObservationsRequest request);

}
