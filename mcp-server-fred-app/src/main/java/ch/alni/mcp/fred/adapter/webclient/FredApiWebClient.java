package ch.alni.mcp.fred.adapter.webclient;

import ch.alni.mcp.fred.port.FileType;
import ch.alni.mcp.fred.port.FredApiClient;
import ch.alni.mcp.fred.port.ObservationsRequest;
import ch.alni.mcp.fred.port.ObservationsResponse;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

@Service
class FredApiWebClient implements FredApiClient {
    private static final Logger LOG = getLogger(FredApiWebClient.class);

    private final WebClient webClient;
    private final FredApiProperties properties;
    private final FredRetryProperties retryProperties;

    FredApiWebClient(FredApiProperties properties, WebClient fredWebClient, FredRetryProperties retryProperties) {
        this.properties = properties;
        this.webClient = fredWebClient;
        this.retryProperties = retryProperties;
    }

    @Override
    public Mono<ObservationsResponse> getObservations(ObservationsRequest request) {
        // Retrieves observations and converts to response
        // Adds required series ID and API key parameters
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(FredApiPaths.OBSERVATIONS_URL)
                        .queryParam("series_id", request.seriesId())
                        .queryParam("api_key", properties.getKey())
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
                .bodyToMono(ObservationsResponse.class)
                .doOnError(throwable -> LOG.error("Cannot retrieve observations from server: {}", throwable.getMessage()))
                .retryWhen(Retry.backoff(retryProperties.getMaxAttempts(), retryProperties.getBackoff())
                        .filter(throwable -> throwable instanceof WebClientResponseException.TooManyRequests)
                );
    }
}
