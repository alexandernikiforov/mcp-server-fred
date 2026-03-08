package ch.alni.mcp.edgar.tools;


import ch.alni.mcp.edgar.service.EdgarService;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Clock;

/**
 * The EdgarMcpServer class provides MCP tools for interacting with the EdgarService. It exposes methods to perform
 * common operations such as resolving a ticker to its CIK (Central Index Key) and checking service availability.
 * <p>
 * This class is designed as a Spring Service and leverages reactive programming through Project Reactor's Mono type for
 * asynchronous, non-blocking responses.
 */
@SuppressWarnings("unused")
@Service
public class EdgarMcpServer {

    private final EdgarService edgarService;

    private final Clock clock;

    EdgarMcpServer(EdgarService edgarService, Clock clock) {
        this.edgarService = edgarService;
        this.clock = clock;
    }

    @McpTool(description = "Returns 'ok'")
    public Mono<String> ping() {
        return Mono.just("ok");
    }

    @McpTool(description = "Returns the CIK for the given ticker")
    public Mono<Long> resolveCik(String ticker) {
        return Mono.just(edgarService.resolveCik(ticker));
    }

}
