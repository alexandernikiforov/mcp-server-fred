package ch.alni.mcp.edgar.service.impl;

import ch.alni.mcp.edgar.core.ReportingPeriod;
import ch.alni.mcp.edgar.port.CompanyFactsResponse;
import ch.alni.mcp.edgar.service.Concept;
import ch.alni.mcp.edgar.service.ConceptFilingHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@AutoConfigureJson
class ConverterTest {

    @Autowired
    private JsonMapper jsonMapper;

    @Value("classpath:responses/aapl-facts.json")
    private Resource facts;

    @Test
    void testFacts() throws IOException {
        final CompanyFactsResponse response = jsonMapper.readValue(facts.getInputStream(), CompanyFactsResponse.class);

        final Optional<ConceptFilingHistory> reportedHistory = Converter
                .toReportedHistory(response, Concept.of("us-gaap:Revenues"), 3);

        assertThat(reportedHistory).isPresent();

    }

    @Test
    void testClassifyPeriod() {
        assertThat(ReportingPeriod.classifyPeriod(LocalDate.parse("2025-09-28"), LocalDate.parse("2026-06-27")))
                .isEqualTo(ReportingPeriod.THREE_QUARTERS);
        assertThat(ReportingPeriod.classifyPeriod(LocalDate.parse("2025-09-28"), LocalDate.parse("2026-03-28")))
                .isEqualTo(ReportingPeriod.TWO_QUARTERS);
        assertThat(ReportingPeriod.classifyPeriod(LocalDate.parse("2025-09-28"), LocalDate.parse("2025-12-27")))
                .isEqualTo(ReportingPeriod.ONE_QUARTER);
        assertThat(ReportingPeriod.classifyPeriod(LocalDate.parse("2024-09-29"), LocalDate.parse("2025-09-27")))
                .isEqualTo(ReportingPeriod.ONE_YEAR);

        assertThat(ReportingPeriod.classifyPeriod(null, LocalDate.parse("2025-09-27")))
                .isEqualTo(ReportingPeriod.UNSPECIFIED);
    }

    @Test
    void testIsSamePeriod() {
        assertThat(ReportingPeriod.isSamePeriod(
                LocalDate.parse("2023-10-01"), LocalDate.parse("2024-09-28"),
                LocalDate.parse("2024-09-29"), LocalDate.parse("2025-09-27")
        )).isTrue();

        assertThat(ReportingPeriod.isSamePeriod(
                LocalDate.parse("2026-03-29"), LocalDate.parse("2026-06-27"),
                LocalDate.parse("2025-03-30"), LocalDate.parse("2025-06-28")
        )).isTrue();

        assertThat(ReportingPeriod.isSamePeriod(
                LocalDate.parse("2025-09-28"), LocalDate.parse("2026-06-27"),
                LocalDate.parse("2024-09-29"), LocalDate.parse("2025-06-28")
        )).isTrue();

        assertThat(ReportingPeriod.isSamePeriod(
                null, LocalDate.parse("2026-06-27"),
                LocalDate.parse("2024-09-29"), LocalDate.parse("2025-06-28")
        )).isFalse();

        assertThat(ReportingPeriod.isSamePeriod(
                null, LocalDate.parse("2026-06-27"),
                null, LocalDate.parse("2025-06-28")
        )).isTrue();

        assertThat(ReportingPeriod.isSamePeriod(
                LocalDate.parse("2026-03-29"), LocalDate.parse("2026-06-27"),
                LocalDate.parse("2024-09-29"), LocalDate.parse("2025-06-28")
        )).isFalse();
    }
}
