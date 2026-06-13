package ch.alni.mcp.fred.tools;

import ch.alni.mcp.fred.service.DateRange;
import ch.alni.mcp.fred.service.FredService;
import ch.alni.mcp.fred.service.LookbackPeriod;
import ch.alni.mcp.fred.service.Series;
import ch.alni.mcp.fred.service.SeriesResponse;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.LocalDate;

@SuppressWarnings("unused")
@Service
public class FredMcpServer {

    private final FredService fredService;
    private final Clock clock;

    public FredMcpServer(FredService fredService, Clock clock) {
        this.fredService = fredService;
        this.clock = clock;
    }

    @McpTool(description = "Returns 'ok'")
    public Mono<String> ping() {
        // test how it works with exceptions
        return Mono.error(new RuntimeException("nok"));
    }

    @McpTool(name = "fred_economic_series_lookback",
            description = "Returns the observation for the given economic series from FRED over the provided lookback interval",
            generateOutputSchema = true,
            annotations = @McpTool.McpAnnotations(
                    title = "Fred Economic Series Lookback",
                    readOnlyHint = true
            ))
    public Mono<SeriesResponse> getSeries(@McpToolParam(description = "The desired economic series to retrieve") Series series,
                                          @McpToolParam(description = "The lookback interval over which to retrieve the series") LookbackPeriod lookbackPeriod) {
        return fredService.getSeries(series, DateRange.of(lookbackPeriod, clock));
    }

    @McpTool(name = "fred_economic_series_last_reading",
            description = "Returns the last available observation from FRED for the given economic series",
            generateOutputSchema = true,
            annotations = @McpTool.McpAnnotations(
                    title = "Fred Economic Series Last Reading",
                    readOnlyHint = true
            ))
    public Mono<SeriesResponse> getSeries(@McpToolParam(description = "The desired economic series to retrieve") Series series) {
        return fredService.getLastReading(series);
    }

    @McpTool(name = "fred_economic_series_date_range",
            description = "Returns observations for the given economic series from FRED over the date range",
            generateOutputSchema = true,
            annotations = @McpTool.McpAnnotations(
                    title = "Fred Economic Series Date Range",
                    readOnlyHint = true
            ))
    public Mono<SeriesResponse> getSeries(@McpToolParam(description = "The desired economic series to retrieve") Series series,
                                          @McpToolParam(description = "The start observation date (inclusive), in ISO date format yyyy-MM-dd")
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                          @McpToolParam(description = "The end observation date (inclusive), in ISO date format yyyy-MM-dd")
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return fredService.getSeries(series, new DateRange(start, end));
    }

}
