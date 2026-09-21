package ch.alni.mcp.edgar.service;

import ch.alni.mcp.edgar.core.BalanceType;
import ch.alni.mcp.edgar.core.PeriodType;

/**
 * TODO: javadoc
 */
public record ConceptMetadata(
        String tag,
        String documentation,
        String dataType,
        PeriodType periodType,
        BalanceType balance,
        boolean abstractConcept,
        boolean deprecated
) {
}
