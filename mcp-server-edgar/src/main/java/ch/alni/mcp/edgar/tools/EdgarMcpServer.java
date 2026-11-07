package ch.alni.mcp.edgar.tools;


import ch.alni.mcp.edgar.core.FiscalYearCountType;
import ch.alni.mcp.edgar.core.Taxonomy;
import ch.alni.mcp.edgar.service.Concept;
import ch.alni.mcp.edgar.service.ConceptFilingHistoryResponse;
import ch.alni.mcp.edgar.service.EdgarService;
import ch.alni.mcp.edgar.service.FilingFactResponse;
import ch.alni.mcp.edgar.service.FilingHistory;
import ch.alni.mcp.edgar.service.FilingStructure;
import ch.alni.mcp.edgar.service.TaxonomyMetadata;
import ch.alni.mcp.edgar.service.TaxonomyService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;

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

    EdgarMcpServer(EdgarService edgarService, TaxonomyService taxonomyService) {
        this.edgarService = edgarService;
        this.taxonomyService = taxonomyService;
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

    @McpTool(name = "resolve_cik",
            description = """
                    Resolves a US stock ticker symbol to the company's SEC Central Index Key (CIK).
                    Use when the company's SEC identifier is needed.
                    """,
            annotations = @McpTool.McpAnnotations(
                    destructiveHint = false,
                    idempotentHint = true,
                    readOnlyHint = true,
                    openWorldHint = false
            ))
    public Mono<Long> resolveCik(
            @McpToolParam(description = "SEC-listed company ticker symbol (e.g., AAPL, MSFT)") String ticker) {
        return Mono.just(edgarService.resolveCik(ticker));
    }

    @McpTool(name = "get_concept_metadata",
            generateOutputSchema = true,
            description = """
                    Returns US-GAAP taxonomy metadata for an XBRL concept,
                    including its documentation, data type, period type, and balance type.
                    Use to understand the accounting meaning and reporting characteristics of a concept before interpreting its values.
                    """,
            annotations = @McpTool.McpAnnotations(
                    destructiveHint = false,
                    idempotentHint = true,
                    readOnlyHint = true,
                    openWorldHint = false
            ))
    public Mono<TaxonomyMetadata> getConceptMetadata(
            @McpToolParam(description = "A supported taxonomy") Taxonomy taxonomy,
            @McpToolParam(description = "The concept such as 'Revenues'") String concept) {
        return Mono.justOrEmpty(taxonomyService.getConceptMetadata(taxonomy, concept));
    }

    @McpTool(name = "get_concept_history",
            generateOutputSchema = true,
            description = """
                    Returns filing-level historical values for specified XBRL concepts reported by a company.
                    Includes reported and comparative periods, filing dates, accession numbers, fiscal years, and fiscal periods.
                    Use to analyze financial trends and compare values reported for the same period across different filings.
                    """,
            annotations = @McpTool.McpAnnotations(
                    destructiveHint = false,
                    idempotentHint = true,
                    readOnlyHint = true
            ))
    public Mono<ConceptFilingHistoryResponse> getConceptHistory(
            @McpToolParam(description = "SEC-listed company ticker symbol (e.g., AAPL, MSFT)") String ticker,
            @McpToolParam(description = "XBRL concepts identified by taxonomy and tag.") Set<Concept> concepts,
            @McpToolParam(description = """
                    Number of fiscal years to include, counting the current fiscal year. This is not the number of filings.
                    """) FiscalYearCountType fiscalYearCount) {
        return edgarService.getReportedHistory(ticker, concepts, fiscalYearCount);
    }

    @McpTool(name = "get_recent_filings",
            generateOutputSchema = true,
            description = """
                    Lists the most recent supported SEC financial filings for a company,
                    including form type, filing date, report date, accession number, and primary document.
                    Supports forms 10-K, 10-Q, and 20-F.
                    Use to locate a filing before retrieving its structure or facts.
                    """,
            annotations = @McpTool.McpAnnotations(
                    destructiveHint = false,
                    idempotentHint = true,
                    readOnlyHint = true
            ))
    public Mono<FilingHistory> getRecentFilings(
            @McpToolParam(description = "SEC-listed company ticker symbol (e.g., AAPL, MSFT)") String ticker,
            @McpToolParam(description = "Maximum number of recent supported filings to return") int limit) {
        return edgarService.getFilingHistory(ticker, limit);
    }

    @McpTool(name = "get_filing_structure",
            generateOutputSchema = true,
            description = """
                    Returns the XBRL presentation structure of a specific company filing,
                    organized into reporting sections and hierarchical concept nodes.
                    Use to discover which concepts appear in financial statements and disclosures before requesting their values.
                    """,
            annotations = @McpTool.McpAnnotations(
                    destructiveHint = false,
                    idempotentHint = true,
                    readOnlyHint = true
            ))
    public Mono<FilingStructure> getFilingStructure(
            @McpToolParam(description = "SEC-listed company ticker symbol (e.g., AAPL, MSFT)") String ticker,
            @McpToolParam(description = "SEC accession number identifying a specific filing.") String accessionNumber) {
        return edgarService.getFilingStructure(ticker, accessionNumber);
    }

    @McpTool(name = "get_filing_facts",
            generateOutputSchema = true,
            description = """
                    Extracts XBRL facts for specified concepts from a single company filing identified by its accession number.
                    Returns values, reporting periods, units, and dimensional contexts.
                    Use for detailed analysis of a specific 10-K, 10-Q, or 20-F filing, including dimension-specific facts.
                    """,
            annotations = @McpTool.McpAnnotations(
                    destructiveHint = false,
                    idempotentHint = true,
                    readOnlyHint = true
            ))
    public Mono<FilingFactResponse> getFilingFacts(
            @McpToolParam(description = "SEC-listed company ticker symbol (e.g., AAPL, MSFT)") String ticker,
            @McpToolParam(description = "SEC accession number identifying a specific filing.") String accessionNumber,
            @McpToolParam(description = "XBRL concepts whose facts should be extracted from the specified filing.") Set<Concept> concepts) {
        return edgarService.getFilingFacts(ticker, accessionNumber, concepts);
    }
}
