package ch.alni.mcp.fred.api.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.webclient.autoconfigure.WebClientAutoConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;

@SpringJUnitConfig(classes = {FredServiceTestConfiguration.class, WebClientAutoConfiguration.class})
@TestPropertySource("classpath:/fred-service-test.properties")
class FredServiceTestSupport {

    @BeforeAll
    static void setUpMockServer() throws IOException {
        MockWebServerProvider.WEB_SERVER.start();
    }

    @AfterAll
    static void tearDownMockServer() {
        MockWebServerProvider.WEB_SERVER.close();
    }
}
