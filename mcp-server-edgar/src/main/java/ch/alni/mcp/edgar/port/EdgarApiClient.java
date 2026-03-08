package ch.alni.mcp.edgar.port;

import reactor.core.publisher.Mono;

/**
 * Client interface for interacting with the SEC Edgar API. This interface provides methods to retrieve data related to
 * SEC submissions, company facts, and specific concepts for a registrant using its Central Index Key (CIK).
 */
public interface EdgarApiClient {

    /**
     * Retrieves SEC submissions data for a given company using its Central Index Key (CIK).
     *
     * @param cik the SEC Central Index Key of the company for which submissions are requested
     * @return a {@link Mono} emitting the {@link CompanySubmissionsResponse} containing submission details, or an error
     * if the request fails
     */
    Mono<CompanySubmissionsResponse> getCompanySubmissions(long cik);

    /**
     * Retrieves SEC company facts data for a given company using its Central Index Key (CIK).
     *
     * @param cik the SEC Central Index Key of the company for which company facts are requested
     * @return a {@link Mono} emitting the {@link CompanyFactsResponse} containing company facts data, or an error if
     * the request fails
     */
    Mono<CompanyFactsResponse> getCompanyFacts(long cik);

    /**
     * Retrieves SEC company concept data for a given company using its Central Index Key (CIK).
     *
     * @param cik      the SEC Central Index Key of the company for which company concept is requested
     * @param taxonomy the taxonomy name, for example {@code us-gaap} or {@code dei}
     * @param concept  the concept name within the taxonomy
     * @return a {@link Mono} emitting the {@link CompanyConceptResponse} containing company concept data, or an error
     * if the request fails
     */
    Mono<CompanyConceptResponse> getCompanyConcept(long cik, String taxonomy, String concept);

    Mono<String> getAccession(long cik, String accession, String primaryDocument);
}
