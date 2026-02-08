package ch.alni.mcp.fred.client.impl;

import ch.alni.mcp.fred.client.*;
import ch.alni.mcp.fred.client.config.FredClientProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
class FredClientServiceImpl implements FredClientService {

    private final WebClient webClient;
    private final FredClientProperties properties;

    FredClientServiceImpl(FredClientProperties properties, WebClient webClient) {
        this.properties = properties;
        this.webClient = webClient;
    }

    @Override
    public Mono<ObservationsResponse> getObservations(ObservationsRequest request) {
        // Retrieves observations and converts to response
        // Adds required series ID and API key parameters
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(FredClientPaths.OBSERVATIONS_URL)
                        .queryParam("series_id", request.seriesId())
                        .queryParam("api_key", properties.getApiKey())
                        .queryParam("file_type", FileType.JSON.getValue())
                        .queryParamIfPresent("realtime_start", Optional.ofNullable(request.realtimeStart()))
                        .queryParamIfPresent("realtime_end", Optional.ofNullable(request.realtimeEnd()))
                        .queryParamIfPresent("limit", request.limit() > 0 ? Optional.of(request.limit()) : Optional.empty())
                        .queryParamIfPresent("offset", request.offset() > 0 ? Optional.of(request.offset()) : Optional.empty())
                        .queryParamIfPresent("sort_order", Optional.ofNullable(request.sortOrder()))
                        .queryParamIfPresent("observation_start", Optional.ofNullable(request.observationStart()))
                        .queryParamIfPresent("observation_end", Optional.ofNullable(request.observationEnd()))
                        .queryParamIfPresent("units", Optional.ofNullable(request.units()))
                        .queryParamIfPresent("frequency", Optional.ofNullable(request.frequency()))
                        .queryParamIfPresent("aggregation_method", Optional.ofNullable(request.aggregationMethod()))
                        .queryParamIfPresent("output_type", Optional.ofNullable(request.outputType()))
                        .queryParamIfPresent("vintage_dates", Optional.ofNullable(request.vintageDates()))
                        .build())
                .retrieve()
                .bodyToMono(ObservationsResponse.class);
    }
}
