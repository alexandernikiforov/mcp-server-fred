package ch.alni.mcp.edgar.service;

import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = {EdgarServiceTestConfiguration.class, JacksonAutoConfiguration.class})
public class EdgarServiceTestSupport {
}
