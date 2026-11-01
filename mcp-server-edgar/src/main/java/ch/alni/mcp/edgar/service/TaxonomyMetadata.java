package ch.alni.mcp.edgar.service;

import ch.alni.mcp.edgar.core.BalanceType;
import ch.alni.mcp.edgar.core.PeriodType;
import lombok.Builder;

/**
 * TODO: javadoc
 */
@Builder
public record TaxonomyMetadata(
        String tag,
        String documentation,
        String dataType,
        PeriodType periodType,
        BalanceType balance
) {
}
