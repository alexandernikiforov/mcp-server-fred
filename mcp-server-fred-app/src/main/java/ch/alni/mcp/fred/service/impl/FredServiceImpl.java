package ch.alni.mcp.fred.service.impl;

import ch.alni.mcp.fred.port.FredApiClient;
import ch.alni.mcp.fred.port.ObservationsRequest;
import ch.alni.mcp.fred.service.DataPoint;
import ch.alni.mcp.fred.service.FredService;
import ch.alni.mcp.fred.service.Spread;
import ch.alni.mcp.fred.service.SpreadResponse;
import ch.alni.mcp.fred.service.Window;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;

@Service
class FredServiceImpl implements FredService {

    public static final String MISSING_OBSERVATION_VALUE = ".";

    private final FredApiClient apiClient;
    private final Clock clock;

    FredServiceImpl(FredApiClient apiClient, Clock clock) {
        this.apiClient = apiClient;
        this.clock = clock;
    }

    @Override
    public Mono<SpreadResponse> getSpread(Spread spread, Window window) {
        final LocalDate endDate = LocalDate.now(clock);
        final LocalDate startDate = endDate.minus(window.getDuration());
        return apiClient.getObservations(ObservationsRequest.builder()
                        .seriesId(spread.name())
                        .observationEnd(endDate)
                        .observationStart(startDate)
                        .build())
                .map(response -> SpreadResponse.builder()
                        .spread(spread)
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
}
