package ch.alni.mcp.edgar.service;

import ch.alni.mcp.edgar.port.EdgarDataClient;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = {EdgarServiceTestConfiguration.class})
@AutoConfigureJson
public class EdgarServiceTestSupport {

    @MockitoBean
    EdgarDataClient edgarDataClient;
}
