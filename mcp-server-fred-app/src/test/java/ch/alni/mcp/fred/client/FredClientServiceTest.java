package ch.alni.mcp.fred.client;

import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

class FredClientServiceTest extends FredClientServiceTestSupport {

    private final MockWebServer server = MockWebServerProvider.WEB_SERVER;
    @Autowired
    private FredClientService service;
    @Value("classpath:/responses/observations-response.json")
    private Resource observationsResponseResource;

    @Test
    void getObservations() throws IOException {
        server.enqueue(new MockResponse.Builder()
                .body(observationsResponseResource.getContentAsString(StandardCharsets.UTF_8))
                .addHeader("Content-Type", "application/json")
                .build()
        );

        final Mono<ObservationsResponse> responseMono = service.getObservations(ObservationsRequest.builder()
                .seriesId("BAMLC0A0CM")
                .build());

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> !response.observations().isEmpty())
                .verifyComplete();
    }
}
