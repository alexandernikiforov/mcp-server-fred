package ch.alni.mcp.edgar.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class EdgarServiceTest extends EdgarServiceTestSupport {

    @Autowired
    private EdgarService service;

    @Test
    void resolveCik() {
        assertThat(service).isNotNull();
        assertThat(service.resolveCik("AAPL")).isEqualTo(320193L);
        assertThat(service.resolveCik("NVDA")).isEqualTo(1045810L);
    }
}
