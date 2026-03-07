package ch.alni.mcp.fred.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;

/**
 * Service for interacting with the FRED API to retrieve economic data.
 */
public interface FredService {

    /**
     * Retrieves time series data for a specified economic data series within a given date range.
     *
     * @param series the economic data series to retrieve; must not be null
     * @param range the date range for which the data is requested; must be valid and not null
     * @return a Mono emitting the SeriesResponse containing the series data and associated metadata
     */
    Mono<SeriesResponse> getSeries(@NotNull Series series, @Valid @NotNull DateRange range);
}
