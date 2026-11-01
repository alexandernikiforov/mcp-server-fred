package ch.alni.mcp.edgar.adapter.webclient;

import io.github.resilience4j.springboot.ratelimiter.autoconfigure.RateLimiterAutoConfiguration;
import mockwebserver3.MockWebServer;
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

    static MockWebServer server = startServer();

    private static MockWebServer startServer() {
        final MockWebServer mockServer = new MockWebServer();
        try {
            mockServer.start();
        }
        catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }

        Runtime.getRuntime().addShutdownHook(
                new Thread(mockServer::close, "edgar-mock-server-shutdown")
        );
        return mockServer;
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        final String baseUrl = "http://localhost:" + server.getPort();
        registry.add("edgar.webclient.archive.base-url", () -> baseUrl);
        registry.add("edgar.webclient.data.base-url", () -> baseUrl);
    }
}
