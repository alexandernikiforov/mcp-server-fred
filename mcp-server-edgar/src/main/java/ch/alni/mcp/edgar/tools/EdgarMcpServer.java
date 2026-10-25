package ch.alni.mcp.edgar.tools;


import ch.alni.mcp.edgar.core.Taxonomy;
import ch.alni.mcp.edgar.core.TaxonomyMetadata;
import ch.alni.mcp.edgar.service.EdgarService;
import ch.alni.mcp.edgar.service.TaxonomyService;
import org.springframework.ai.mcp.annotation.McpTool;
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
    private final TaxonomyService taxonomyService;

    private final Clock clock;

    EdgarMcpServer(EdgarService edgarService, TaxonomyService taxonomyService, Clock clock) {
        this.edgarService = edgarService;
        this.taxonomyService = taxonomyService;
        this.clock = clock;
    }

    @McpTool(description = "Returns 'ok'", annotations = @McpTool.McpAnnotations(
            destructiveHint = false,
            idempotentHint = true,
            readOnlyHint = true,
            openWorldHint = false
    ))
    public Mono<String> ping() {
        return Mono.just("ok");
    }

    @McpTool(description = "Returns the CIK for the given ticker", annotations = @McpTool.McpAnnotations(
            destructiveHint = false,
            idempotentHint = true,
            readOnlyHint = true,
            openWorldHint = false
    ))
    public Mono<Long> resolveCik(String ticker) {
        return Mono.just(edgarService.resolveCik(ticker));
    }

    @McpTool(name = "get_concept_metadata",
            generateOutputSchema = true,
            description = "Returns metadata for the given taxonomy and concept, including description",
            annotations = @McpTool.McpAnnotations(
                    destructiveHint = false,
                    idempotentHint = true,
                    readOnlyHint = true,
                    openWorldHint = false
            ))
    public Mono<TaxonomyMetadata> getConceptMetadata(Taxonomy taxonomy, String concept) {
        return Mono.justOrEmpty(taxonomyService.getConceptMetadata(taxonomy, concept));
    }

}
