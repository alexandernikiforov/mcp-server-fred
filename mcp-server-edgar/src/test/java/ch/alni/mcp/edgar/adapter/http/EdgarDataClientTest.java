package ch.alni.mcp.edgar.adapter.http;

import ch.alni.mcp.edgar.port.CompanyConceptResponse;
import ch.alni.mcp.edgar.port.CompanyFact;
import ch.alni.mcp.edgar.port.CompanyFactsResponse;
import ch.alni.mcp.edgar.port.CompanySubmissionsResponse;
import ch.alni.mcp.edgar.port.EdgarDataClient;
import mockwebserver3.MockResponse;
import mockwebserver3.RecordedRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class EdgarDataClientTest extends EdgarClientTestSupport {

    @Value("classpath:/responses/CIK0000320193-facts.json")
    private Resource companyFactsResponseResource;

    @Value("classpath:/responses/CIK0000320193-concept-us-gaap-assets.json")
    private Resource companyConceptResponseResource;

    @Value("classpath:/responses/CIK0000320193-submissions.json")
    private Resource submissionsResponseResource;

    @Autowired
    private EdgarDataClient client;

    @Test
    void getCompanyFacts() throws Exception {
        server.enqueue(new MockResponse.Builder()
                .body(companyFactsResponseResource.getContentAsString(StandardCharsets.UTF_8))
                .addHeader("Content-Type", "application/json")
                .build()
        );

        final Mono<CompanyFactsResponse> responseMono = client.getCompanyFacts(320193);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> {
                    assertThat(response.cik()).isEqualTo(320193);
                    assertThat(response.entityName()).isEqualTo("Apple Inc.");
                    assertThat(response.facts()).hasSize(2);

                    final Map<String, CompanyFactsResponse.FactDefinition> dei = response.facts().get("dei");
                    assertThat(dei.keySet()).containsExactlyInAnyOrder("EntityCommonStockSharesOutstanding", "EntityPublicFloat");

                    final Map<String, CompanyFactsResponse.FactDefinition> gaap = response.facts().get("us-gaap");
                    assertThat(gaap.keySet()).containsExactlyInAnyOrder("AccountsPayable", "AccountsPayableCurrent");

                    return true;
                })
                .verifyComplete();

        // check request
        final RecordedRequest recordedRequest = server.takeRequest(10, TimeUnit.SECONDS);
        assertThat(recordedRequest).isNotNull();

        assertThat(recordedRequest.getUrl().host()).isEqualTo("localhost");
        assertThat(recordedRequest.getUrl().encodedPath()).isEqualTo(
                EdgarApiPaths.COMPANY_FACTS_PATH.replace("{cik}", CikFormatter.formatCik(320193)));
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
    }

    @Test
    void getCompanyConcept() throws Exception {
        server.enqueue(new MockResponse.Builder()
                .body(companyConceptResponseResource.getContentAsString(StandardCharsets.UTF_8))
                .addHeader("Content-Type", "application/json")
                .build()
        );

        final String taxonomy = "us-gaap";
        final String concept = "Assets";
        final Mono<CompanyConceptResponse> responseMono = client.getCompanyConcept(320193, taxonomy, concept);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> {
                    assertThat(response.cik()).isEqualTo(320193);
                    assertThat(response.entityName()).isEqualTo("Apple Inc.");
                    assertThat(response.units()).hasSize(1);

                    final List<CompanyFact> facts = response.units().get("USD");
                    assertThat(facts).isNotEmpty();

                    return true;
                })
                .verifyComplete();

        // check request
        final RecordedRequest recordedRequest = server.takeRequest(10, TimeUnit.SECONDS);
        assertThat(recordedRequest).isNotNull();

        assertThat(recordedRequest.getUrl().host()).isEqualTo("localhost");
        assertThat(recordedRequest.getUrl().encodedPath()).isEqualTo(
                EdgarApiPaths.COMPANY_CONCEPT_PATH
                        .replace("{cik}", CikFormatter.formatCik(320193))
                        .replace("{taxonomy}", taxonomy)
                        .replace("{concept}", concept));
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
    }

    @Test
    void getSubmissions() throws Exception {
        server.enqueue(new MockResponse.Builder()
                .body(submissionsResponseResource.getContentAsString(StandardCharsets.UTF_8))
                .addHeader("Content-Type", "application/json")
                .build()
        );

        final Mono<CompanySubmissionsResponse> responseMono = client.getCompanySubmissions(320193);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> {
                    assertThat(response.cik()).isEqualTo(CikFormatter.formatCik(320193));
                    assertThat(response.name()).isEqualTo("Apple Inc.");

                    final CompanySubmissionsResponse.RecentFilings recentFilings = response.filings().recent();
                    assertThat(recentFilings).isNotNull();
                    assertThat(recentFilings.accessionNumber()).isNotEmpty();
                    assertThat(recentFilings.filingDate()).isNotEmpty();
                    assertThat(recentFilings.form()).isNotEmpty();
                    return true;
                })
                .verifyComplete();

        // check request
        final RecordedRequest recordedRequest = server.takeRequest(10, TimeUnit.SECONDS);
        assertThat(recordedRequest).isNotNull();

        assertThat(recordedRequest.getUrl().host()).isEqualTo("localhost");
        assertThat(recordedRequest.getUrl().encodedPath()).isEqualTo(
                EdgarApiPaths.SUBMISSIONS_PATH.replace("{cik}", CikFormatter.formatCik(320193)));
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
    }

    @Test
    void getSubmissionsIfForbidden() {
        server.enqueue(new MockResponse.Builder()
                .code(403)
                .body("<html><body>SEC.gov | Your Request Originates from an Undeclared Automated Tool</body></html>")
                .addHeader("Content-Type", "text/html; charset=UTF-8")
                .build()
        );

        final Mono<CompanySubmissionsResponse> responseMono = client.getCompanySubmissions(320193);

        StepVerifier.create(responseMono)
                .expectError(WebClientResponseException.Forbidden.class)
                .verify();
    }

}
