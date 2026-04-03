package ch.alni.mcp.edgar.port;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.util.List;
import java.util.Map;

/**
 * Partial SEC company facts response.
 *
 * <p>This model keeps the stable top-level structure strongly typed while leaving
 * taxonomy and concept names dynamic via maps. That matches the SEC payload well without forcing the application to
 * model every possible fact explicitly.</p>
 *
 * @param cik        SEC Central Index Key
 * @param entityName registrant name
 * @param facts      facts grouped by taxonomy and concept name
 */
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanyFactsResponse(
        long cik,
        String entityName,
        Map<String, Map<String, FactDefinition>> facts
) {

    /**
     * A reported concept definition inside a taxonomy, for example a us-gaap fact.
     *
     * @param label       human-readable concept label
     * @param description concept description
     * @param units       reported values grouped by unit, for example USD or shares
     */
    @Builder(toBuilder = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FactDefinition(
            String label,
            String description,
            Map<String, List<CompanyFact>> units
    ) {
    }

}
