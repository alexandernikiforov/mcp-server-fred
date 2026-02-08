package ch.alni.mcp.fred.client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

@SpringJUnitConfig(classes = FredClientServiceTestConfiguration.class)
@TestPropertySource("classpath:/fred-client-test.properties")
class FredClientServiceTestSupport {
    private static final Logger LOG = getLogger(FredClientServiceTestSupport.class);

    @BeforeAll
    static void setUpMockServer() throws IOException {
        LOG.info("Setting up mock server");
        MockWebServerProvider.WEB_SERVER.start();
    }

    @AfterAll
    static void tearDownMockServer() {
        MockWebServerProvider.WEB_SERVER.close();
    }
}
