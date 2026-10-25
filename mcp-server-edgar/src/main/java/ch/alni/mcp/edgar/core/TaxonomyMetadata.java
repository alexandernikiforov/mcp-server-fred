package ch.alni.mcp.edgar.core;

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
