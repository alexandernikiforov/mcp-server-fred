package ch.alni.mcp.fred.tools;

import ch.alni.mcp.fred.service.FredService;
import ch.alni.mcp.fred.service.Spread;
import ch.alni.mcp.fred.service.SpreadResponse;
import ch.alni.mcp.fred.service.Window;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
@Service
public class FredMcpServer {

    private final FredService fredService;

    public FredMcpServer(FredService fredService) {
        this.fredService = fredService;
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
        return fredService.getSpread(spread, window);
    }
}
