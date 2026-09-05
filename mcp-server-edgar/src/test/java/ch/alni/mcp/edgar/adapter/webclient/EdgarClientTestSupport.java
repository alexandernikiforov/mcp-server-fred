package ch.alni.mcp.edgar.adapter.webclient;

import io.github.resilience4j.springboot.ratelimiter.autoconfigure.RateLimiterAutoConfiguration;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.webclient.test.autoconfigure.WebClientTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;

@WebClientTest
@SpringJUnitConfig(classes = {EdgarWebClientConfiguration.class, EdgarClientTestConfiguration.class})
@ImportAutoConfiguration(RateLimiterAutoConfiguration.class)
@TestPropertySource("classpath:/edgar-client-test.properties")
abstract class EdgarClientTestSupport {

    static final MockWebServer server = new MockWebServer();

    @BeforeAll
    static void setUpMockServer() throws IOException {
        server.start();
    }

    @AfterAll
    static void tearDownMockServer() {
        server.close();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        final String baseUrl = "http://localhost:" + server.getPort();
        registry.add("edgar.webclient.archive.base-url", () -> baseUrl);
        registry.add("edgar.webclient.data.base-url", () -> baseUrl);
    }
}
