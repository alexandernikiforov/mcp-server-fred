package ch.alni.mcp.edgar.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an association between a Central Index Key (CIK), a ticker symbol, and the corresponding title. This
 * record is primarily used to map and manage identifying information for entities such as companies in financial or
 * regulatory contexts.
 *
 * @param cik    The Central Index Key (CIK), a unique identifier assigned to entities in SEC filings.
 * @param ticker The ticker symbol representing the entity in financial markets.
 * @param title  The title or name of the entity associated with the CIK and ticker.
 */
public record CikTickerMapping(
        @JsonProperty("cik_str")
        long cik,
        String ticker,
        String title
) {
}
