package ch.alni.mcp.fred.adapter.http;

import ch.alni.mcp.fred.port.FredApiClient;
import ch.alni.mcp.fred.port.Observation;
import ch.alni.mcp.fred.port.ObservationsRequest;
import ch.alni.mcp.fred.port.ObservationsResponse;
import mockwebserver3.MockResponse;
import mockwebserver3.RecordedRequest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

class FredApiClientTest extends FredApiClientTestSupport {

    private static final Logger LOG = getLogger(FredApiClientTest.class);
    private static final String SERIES_ID = "BAMLC0A0CM";

    @Autowired
    private FredApiClient client;

    @Autowired
    private FredApiProperties properties;

    @Autowired
    private FredRetryProperties retryProperties;

    @Value("classpath:/responses/observations-response.json")
    private Resource observationsResponseResource;

    @Value("classpath:/responses/observations-response-400.json")
    private Resource observationsResponseBadRequestResource;

    @Value("classpath:/responses/observations-response-429.json")
    private Resource observationsResponseTooManyRequestsResource;

    @Test
    void getObservations() throws Exception {
        LOG.info("getObservations");

        server.enqueue(new MockResponse.Builder()
                .body(observationsResponseResource.getContentAsString(StandardCharsets.UTF_8))
                .addHeader("Content-Type", "application/json")
                .build()
        );

        final Mono<ObservationsResponse> responseMono = client.getObservations(ObservationsRequest.builder()
                .seriesId(SERIES_ID)
                .build());

        // check response
        StepVerifier.create(responseMono)
                .expectNextMatches(response -> {
                    assertThat(response.observations()).isNotEmpty();
                    assertThat(response.observations().get(0)).isEqualTo(Observation.builder()
                            .realtimeStart(LocalDate.of(2026, Month.FEBRUARY, 9))
                            .realtimeEnd(LocalDate.of(2026, Month.FEBRUARY, 9))
                            .date(LocalDate.of(2025, Month.NOVEMBER, 10))
                            .value("0.82")
                            .build());
                    return true;
                })
                .verifyComplete();

        // check request
        final RecordedRequest recordedRequest = server.takeRequest(10, TimeUnit.SECONDS);
        assertThat(recordedRequest).isNotNull();

        assertThat(recordedRequest.getUrl().host()).isEqualTo("localhost");
        assertThat(recordedRequest.getUrl().encodedPath()).isEqualTo(FredApiPaths.OBSERVATIONS_URL);
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getRequestLine())
                .contains("series_id=" + SERIES_ID)
                .contains("api_key=" + properties.getKey());
    }

    @Test
    void getObservationsBadRequest() throws Exception {
        LOG.info("getObservationsBadRequest");

        server.enqueue(new MockResponse.Builder()
                .code(400)
                .body(observationsResponseBadRequestResource.getContentAsString(StandardCharsets.UTF_8))
                .addHeader("Content-Type", "application/json")
                .build()
        );

        final Mono<ObservationsResponse> responseMono = client.getObservations(ObservationsRequest.builder()
                .seriesId(SERIES_ID)
                .build());

        // check response
        StepVerifier.create(responseMono)
                .expectError(WebClientResponseException.BadRequest.class)
                .verify();
    }

    @Test
    void getObservationsTooManyRequests() throws Exception {
        LOG.info("getObservationsTooManyRequests");

        server.enqueue(new MockResponse.Builder()
                .code(429)
                .body(observationsResponseTooManyRequestsResource.getContentAsString(StandardCharsets.UTF_8))
                .addHeader("Content-Type", "application/json")
                .build()
        );

        server.enqueue(new MockResponse.Builder()
                .body(observationsResponseResource.getContentAsString(StandardCharsets.UTF_8))
                .addHeader("Content-Type", "application/json")
                .build()
        );

        final Mono<ObservationsResponse> responseMono = client.getObservations(ObservationsRequest.builder()
                .seriesId(SERIES_ID)
                .build());

        // check response
        StepVerifier.create(responseMono)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getObservationsTooManyRequestsFollowedByInternalServerError() throws Exception {
        LOG.info("getObservationsTooManyRequestsFollowedByInternalServerError");

        server.enqueue(new MockResponse.Builder()
                .code(429)
                .body(observationsResponseTooManyRequestsResource.getContentAsString(StandardCharsets.UTF_8))
                .addHeader("Content-Type", "application/json")
                .build()
        );

        server.enqueue(new MockResponse.Builder()
                .code(500)
                .build()
        );

        final Mono<ObservationsResponse> responseMono = client.getObservations(ObservationsRequest.builder()
                .seriesId(SERIES_ID)
                .build());

        // check response
        StepVerifier.create(responseMono)
                .expectError(WebClientResponseException.InternalServerError.class)
                .verify();
    }

    @Test
    void getObservationsTooManyRequestsRetriesExhausted() throws Exception {
        LOG.info("getObservationsTooManyRequestsRetriesExhausted");

        final int totalAttempts = 1 + retryProperties.getMaxAttempts();

        for (int i = 0; i < totalAttempts; i++) {
            server.enqueue(new MockResponse.Builder()
                    .code(429)
                    .body(observationsResponseTooManyRequestsResource.getContentAsString(StandardCharsets.UTF_8))
                    .addHeader("Content-Type", "application/json")
                    .build()
            );
        }

        final Mono<ObservationsResponse> responseMono = client.getObservations(ObservationsRequest.builder()
                .seriesId(SERIES_ID)
                .build());

        // check response
        StepVerifier.create(responseMono)
                // when retries are exhausted, an IllegalStateException is thrown
                .expectError(IllegalStateException.class)
                .verify();
    }

}
