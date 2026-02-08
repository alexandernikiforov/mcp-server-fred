package ch.alni.mcp.fred.client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;

@SpringJUnitConfig(classes = FredClientServiceTestConfiguration.class)
@TestPropertySource("classpath:/fred-client-test.properties")
class FredClientServiceTestSupport {

    @BeforeAll
    static void setUpMockServer() throws IOException {
        MockWebServerProvider.WEB_SERVER.start();
    }

    @AfterAll
    static void tearDownMockServer() {
        MockWebServerProvider.WEB_SERVER.close();
    }
}
