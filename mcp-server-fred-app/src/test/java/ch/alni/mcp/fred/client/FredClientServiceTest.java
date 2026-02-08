package ch.alni.mcp.fred.client;

import ch.alni.mcp.fred.client.config.FredClientProperties;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

class FredClientServiceTest extends FredClientServiceTestSupport {

    private static final Logger LOG = getLogger(FredClientServiceTest.class);

    private static final String SERIES_ID = "BAMLC0A0CM";
    private final MockWebServer server = MockWebServerProvider.WEB_SERVER;

    @Autowired
    private FredClientService service;

    @Autowired
    private FredClientProperties properties;

    @Value("classpath:/responses/observations-response.json")
    private Resource observationsResponseResource;

    @Test
    void getObservations() throws Exception {
        LOG.info("getObservations");

        server.enqueue(new MockResponse.Builder()
                .body(observationsResponseResource.getContentAsString(StandardCharsets.UTF_8))
                .addHeader("Content-Type", "application/json")
                .build()
        );

        final Mono<ObservationsResponse> responseMono = service.getObservations(ObservationsRequest.builder()
                .seriesId(SERIES_ID)
                .build());

        // check response
        StepVerifier.create(responseMono)
                .expectNextMatches(response -> !response.observations().isEmpty())
                .verifyComplete();

        // check request
        final RecordedRequest recordedRequest = server.takeRequest(10, TimeUnit.SECONDS);
        assertThat(recordedRequest).isNotNull();

        assertThat(recordedRequest.getUrl().host()).isEqualTo("localhost");
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getRequestLine())
                .contains("series_id=" + SERIES_ID)
                .contains("api_key=" + properties.getApiKey());
    }
}
