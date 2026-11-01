package ch.alni.mcp.edgar.port;

import reactor.core.publisher.Mono;

/**
 * Client interface for interacting with the SEC Edgar Archive API.
 */
public interface EdgarArchiveClient {

    /**
     * Retrieves an accessioned document from the SEC Edgar Archive.
     *
     * @param cik             the SEC Central Index Key
     * @param accession       the accession number without dashes
     * @param primaryDocument the primary document name
     * @return a {@link Mono} emitting the document content as a string, or an error if the request fails
     */
    Mono<String> getAccession(long cik, String accession, String primaryDocument);

    /**
     * Retrieves the directory listing for an accessioned document from the SEC Edgar Archive.
     *
     * @param cik       the SEC Central Index Key
     * @param accession the accession number without dashes
     * @return a {@link Mono} emitting the directory listing, or an error if the request fails
     */
    Mono<DirectoryResponse> getDirectory(long cik, String accession);

}
