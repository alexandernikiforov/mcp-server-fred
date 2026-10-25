package ch.alni.mcp.edgar.core;

import lombok.Builder;

/**
 * Represents a concept within a taxonomy framework used for financial or business data reporting. A taxonomy concept
 * defines the attributes and metadata required for classification and interpretation of data entries.
 *
 * @param id         unique identifier for the taxonomy concept
 * @param name       descriptive name of the concept
 * @param type       the type or category of the concept
 * @param periodType specifies whether the concept is applicable to a point in time ({@link PeriodType#INSTANT}) or
 *                   covers a span of time ({@link PeriodType#DURATION})
 * @param balance    indicates the natural balance type of the concept, such as {@link BalanceType#DEBIT},
 *                   {@link BalanceType#CREDIT}, or {@link BalanceType#NONE} if none is applicable
 */
@Builder
public record TaxonomyConcept(
        String id,
        String name,
        String type,
        PeriodType periodType,
        BalanceType balance
) {
}
