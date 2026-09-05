package ch.alni.mcp.edgar.adapter.webclient;

import ch.alni.mcp.edgar.port.CompanyConceptResponse;
import ch.alni.mcp.edgar.port.CompanyFactsResponse;
import ch.alni.mcp.edgar.port.CompanySubmissionsResponse;
import ch.alni.mcp.edgar.port.EdgarDataClient;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
class EdgarDataHttpClient implements EdgarDataClient {

    private final WebClient dataWebClient;

    EdgarDataHttpClient(WebClient edgarDataWebClient) {
        this.dataWebClient = edgarDataWebClient;
    }

    @Override
    @RateLimiter(name = "edgar")
    public Mono<CompanySubmissionsResponse> getCompanySubmissions(long cik) {
        return dataWebClient.get()
                .uri(EdgarDataPaths.SUBMISSIONS_PATH, CikFormatter.formatCik(cik))
                .retrieve()
                .bodyToMono(CompanySubmissionsResponse.class);
    }

    @Override
    @RateLimiter(name = "edgar")
    public Mono<CompanyFactsResponse> getCompanyFacts(long cik) {
        return dataWebClient.get()
                .uri(EdgarDataPaths.COMPANY_FACTS_PATH, CikFormatter.formatCik(cik))
                .retrieve()
                .bodyToMono(CompanyFactsResponse.class);
    }

    @Override
    @RateLimiter(name = "edgar")
    public Mono<CompanyConceptResponse> getCompanyConcept(long cik, String taxonomy, String concept) {
        return dataWebClient.get()
                .uri(EdgarDataPaths.COMPANY_CONCEPT_PATH, CikFormatter.formatCik(cik), taxonomy, concept)
                .retrieve()
                .bodyToMono(CompanyConceptResponse.class);
    }
}
