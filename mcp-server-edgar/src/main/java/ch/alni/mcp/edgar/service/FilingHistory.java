package ch.alni.mcp.edgar.service;

import java.time.LocalDate;
import java.util.List;

public record FilingHistory(List<Filing> filings) {

    public record Filing(
            String accessionNumber,
            LocalDate filingDate,
            LocalDate reportDate,
            String form,
            String primaryDocument
    ) {
    }

}
