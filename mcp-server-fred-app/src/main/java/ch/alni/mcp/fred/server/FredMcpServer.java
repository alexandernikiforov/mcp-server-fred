package ch.alni.mcp.fred.server;

import ch.alni.mcp.fred.service.FredService;
import ch.alni.mcp.fred.service.ObservationsRequest;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;

@SuppressWarnings("unused")
@Service
public class FredMcpServer {

    public static final String MISSING_OBSERVATION_VALUE = ".";
    private final FredService clientService;
    private final Clock clock;

    public FredMcpServer(FredService clientService, Clock clock) {
        this.clientService = clientService;
        this.clock = clock;
    }

    @McpTool(description = "Returns 'ok'")
    public Mono<String> ping() {
        // test how it works with exceptions
        return Mono.error(new RuntimeException("nok"));
    }

    @McpTool(name = "fred_spread_window",
            description = "Returns the observation for the given spread over the provided window",
            generateOutputSchema = true)
    public Mono<SpreadResponse> getSpread(@McpToolParam(description = "The desired option spread to retrieve") Spread spread,
                                          @McpToolParam(description = "The window over which to retrieve the spread") Window window) {
        final LocalDate endDate = LocalDate.now(clock);
        final LocalDate startDate = endDate.minus(window.getDuration());
        return clientService.getObservations(ObservationsRequest.builder()
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
