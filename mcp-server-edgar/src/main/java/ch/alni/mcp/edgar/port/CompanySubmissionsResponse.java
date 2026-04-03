package ch.alni.mcp.edgar.port;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Partial SEC submissions response.
 *
 * <p>This model intentionally captures only the fields currently needed by the application.
 * Unknown JSON properties are ignored so the SEC payload can evolve without forcing the application to mirror the
 * entire response structure.</p>
 *
 * @param cik            SEC Central Index Key
 * @param entityType     SEC entity type
 * @param sic            SIC code
 * @param sicDescription SIC description
 * @param name           registrant name
 * @param tickers        registered ticker symbols
 * @param exchanges      listed exchanges
 * @param filings        filing collections and metadata
 */
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanySubmissionsResponse(
        String cik,
        String entityType,
        String sic,
        String sicDescription,
        String name,
        List<String> tickers,
        List<String> exchanges,
        Filings filings
) {

    /**
     * Filing collections from the SEC submissions response.
     *
     * @param recent recent filings in SEC columnar-array format
     * @param files  additional archived filing index files
     */
    @Builder(toBuilder = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Filings(
            RecentFilings recent,
            List<FilingFile> files
    ) {
    }

    /**
     * Recent filings in SEC-native columnar format.
     *
     * <p>The SEC represents recent filings as parallel arrays where items at the same index
     * belong to the same filing.</p>
     *
     * @param accessionNumber       accession numbers
     * @param filingDate            filing dates as ISO-8601 strings
     * @param reportDate            report dates as ISO-8601 strings
     * @param acceptanceDateTime    acceptance timestamps as ISO-8601 strings
     * @param form                  form types
     * @param primaryDocument       primary document names
     * @param primaryDocDescription primary document descriptions
     */
    @Builder(toBuilder = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RecentFilings(
            List<String> accessionNumber,
            List<LocalDate> filingDate,
            List<String> reportDate,
            List<Instant> acceptanceDateTime,
            List<String> form,
            List<String> primaryDocument,
            List<String> primaryDocDescription
    ) {
    }

    /**
     * Additional SEC filing index file metadata.
     *
     * @param name        archive file name
     * @param filingCount number of filings in the archive
     * @param filingFrom  lower filing date bound
     * @param filingTo    upper filing date bound
     */
    @Builder(toBuilder = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FilingFile(
            String name,
            int filingCount,
            String filingFrom,
            String filingTo
    ) {
    }
}
