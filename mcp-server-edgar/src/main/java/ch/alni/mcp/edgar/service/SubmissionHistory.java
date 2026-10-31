package ch.alni.mcp.edgar.service;

import java.time.LocalDate;
import java.util.List;

public record SubmissionHistory(List<Submission> submissions) {

    public record Submission(String accessionNumber, LocalDate filingDate, String form, String primaryDocument) {
    }

}
