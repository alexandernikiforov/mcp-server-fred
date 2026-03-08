package ch.alni.mcp.edgar.adapter.webclient;

import ch.alni.mcp.edgar.port.CompanyConceptResponse;
import ch.alni.mcp.edgar.port.CompanyFactsResponse;
import ch.alni.mcp.edgar.port.CompanySubmissionsResponse;
import ch.alni.mcp.edgar.port.EdgarApiClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
class EdgarApiWebClient implements EdgarApiClient {

    private final WebClient dataWebClient;
    private final WebClient archiveWebClient;

    EdgarApiWebClient(WebClient edgarDataWebClient, WebClient edgarArchiveWebClient) {
        this.dataWebClient = edgarDataWebClient;
        this.archiveWebClient = edgarArchiveWebClient;
    }

    private static String formatCik(long cik) {
        return String.format("%010d", cik);
    }

    @Override
    public Mono<CompanySubmissionsResponse> getCompanySubmissions(long cik) {
        return dataWebClient.get()
                .uri(EdgarApiPaths.SUBMISSIONS_PATH, formatCik(cik))
                .retrieve()
                .bodyToMono(CompanySubmissionsResponse.class);
    }

    @Override
    public Mono<CompanyFactsResponse> getCompanyFacts(long cik) {
        return dataWebClient.get()
                .uri(EdgarApiPaths.COMPANY_FACTS_PATH, formatCik(cik))
                .retrieve()
                .bodyToMono(CompanyFactsResponse.class);
    }

    @Override
    public Mono<CompanyConceptResponse> getCompanyConcept(long cik, String taxonomy, String concept) {
        return dataWebClient.get()
                .uri(EdgarApiPaths.COMPANY_CONCEPT_PATH, formatCik(cik), taxonomy, concept)
                .retrieve()
                .bodyToMono(CompanyConceptResponse.class);
    }

    @Override
    public Mono<String> getAccession(long cik, String accession, String primaryDocument) {
        return archiveWebClient.get()
                .uri(EdgarApiPaths.ACCESSION_PATH, formatCik(cik), accession, primaryDocument)
                .retrieve()
                .bodyToMono(String.class);
    }
}
