package ch.alni.mcp.fred.service.impl;

import ch.alni.mcp.fred.port.FredApiClient;
import ch.alni.mcp.fred.port.ObservationsRequest;
import ch.alni.mcp.fred.service.DataPoint;
import ch.alni.mcp.fred.service.DateRange;
import ch.alni.mcp.fred.service.FredService;
import ch.alni.mcp.fred.service.LookbackPeriod;
import ch.alni.mcp.fred.service.Series;
import ch.alni.mcp.fred.service.SeriesResponse;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.util.Objects;

@Service
@Validated
class FredServiceImpl implements FredService {

    public static final String MISSING_OBSERVATION_VALUE = ".";

    private final FredApiClient apiClient;

    private final Clock clock;

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
}
