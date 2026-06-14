package ch.alni.mcp.fred.service.impl;

import ch.alni.mcp.fred.port.FredApiClient;
import ch.alni.mcp.fred.port.ObservationsRequest;
import ch.alni.mcp.fred.service.DataPoint;
import ch.alni.mcp.fred.service.DateRange;
import ch.alni.mcp.fred.service.FredService;
import ch.alni.mcp.fred.service.Language;
import ch.alni.mcp.fred.service.LookbackPeriod;
import ch.alni.mcp.fred.service.Series;
import ch.alni.mcp.fred.service.SeriesResponse;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.EnumSet;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

@Service
@Validated
class FredServiceImpl implements FredService {
    private static final Logger LOG = getLogger(FredServiceImpl.class);

    public static final String MISSING_OBSERVATION_VALUE = ".";

    private final FredApiClient apiClient;

    private final Clock clock;

    @Value("classpath:/prompts/credit-regime.txt")
    private Resource creditRegimePrompt;

    FredServiceImpl(FredApiClient apiClient, Clock clock) {
        this.apiClient = apiClient;
        this.clock = clock;
    }

    @Override
    public Mono<SeriesResponse> getSeries(Series series, DateRange range) {
        return apiClient.getObservations(ObservationsRequest.builder()
                        .seriesId(series.name())
                        .observationEnd(range.end())
                        .observationStart(range.start())
                        .build())
                .map(response -> SeriesResponse.builder()
                        .series(series)
                        .startDate(response.observationStart())
                        .endDate(response.observationEnd())
                        .points(response.observations().stream()
                                // FRED sends "." for missing observations
                                .filter(observation -> !Objects.equals(observation.value(), MISSING_OBSERVATION_VALUE))
                                .map(observation -> DataPoint.builder()
                                        .date(observation.date())
                                        .value(observation.value())
                                        .build())
                                .toList())
                        .build());
    }

    @Override
    public Mono<SeriesResponse> getLastReading(Series series) {
        final DateRange range = DateRange.of(LookbackPeriod.LAST_1M, clock);
        return apiClient.getObservations(ObservationsRequest.builder()
                        .seriesId(series.name())
                        .observationEnd(range.end())
                        .observationStart(range.start())
                        .build())
                .map(response -> SeriesResponse.builder()
                        .series(series)
                        .startDate(response.observationStart())
                        .endDate(response.observationEnd())
                        .points(response.observations().stream()
                                // FRED sends "." for missing observations
                                .filter(observation -> !Objects.equals(observation.value(), MISSING_OBSERVATION_VALUE))
                                .reduce((_, second) -> second)
                                .map(observation -> DataPoint.builder()
                                        .date(observation.date())
                                        .value(observation.value())
                                        .build())
                                .stream()
                                .toList())
                        .build());
    }

    @Override
    public Flux<Series> listSeries() {
        return Flux.fromIterable(EnumSet.allOf(Series.class));
    }

    @Override
    public Mono<String> getCreditRegimePrompt(Language language) {
        return Mono.fromCallable(() -> creditRegimePrompt.getContentAsString(StandardCharsets.UTF_8)
                        .replace("{language}", language.getLang()))
                .doOnError(e -> LOG.error("Failed to read credit regime prompt", e));
    }
}
