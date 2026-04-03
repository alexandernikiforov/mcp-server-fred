package ch.alni.mcp.edgar.port;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.util.List;
import java.util.Map;

/**
 * SEC company concept response for a single taxonomy and concept.
 *
 * <p>This model represents the EDGAR company-concept endpoint, which returns one
 * concept for one company, grouped by reporting unit.</p>
 *
 * @param cik         SEC Central Index Key
 * @param taxonomy    taxonomy name, for example {@code us-gaap} or {@code dei}
 * @param tag         concept name within the taxonomy
 * @param label       human-readable concept label
 * @param description concept description
 * @param entityName  registrant name
 * @param units       reported values grouped by unit, for example {@code USD} or {@code shares}
 */
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanyConceptResponse(
        long cik,
        String taxonomy,
        String tag,
        String label,
        String description,
        String entityName,
        Map<String, List<CompanyFact>> units
) {
}
