package ch.alni.mcp.fred.adapter.http;

import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.webclient.autoconfigure.WebClientAutoConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;

@SpringJUnitConfig(classes = {FredApiClientTestConfiguration.class, WebClientAutoConfiguration.class})
@TestPropertySource("classpath:/fred-api-client-test.properties")
class FredApiClientTestSupport {

    protected static final MockWebServer server = new MockWebServer();

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
        registry.add("fred.mock.port", server::getPort);
    }

}
