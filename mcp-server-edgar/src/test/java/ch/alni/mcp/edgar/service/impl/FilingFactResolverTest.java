package ch.alni.mcp.edgar.service.impl;

import ch.alni.mcp.edgar.service.Concept;
import ch.alni.mcp.edgar.service.FilingFactResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = {
        FilingFactResolver.class
})
class FilingFactResolverTest {

    @Value("classpath:/responses/baba-20260331_htm.xml")
    private Resource babaInstanceXml;

    @Value("classpath:/responses/aapl-20260926_htm.xml")
    private Resource aaplInstanceXml;

    @Value("classpath:/responses/aapl-20180929.xml")
    private Resource aaplOldInstanceXml;

    @Autowired
    private FilingFactResolver resolver;

    @Test
    void testResolveOnBaba() throws IOException {
        final String contentAsString = babaInstanceXml.getContentAsString(StandardCharsets.UTF_8);
        final String concept = "baba:PaymentsToAcquireLandUseRightsPropertyAndEquipment";

        final Optional<FilingFactResponse> factResponse = resolver.parseFiling(contentAsString, Set.of(Concept.of(concept)));

        assertThat(factResponse).isNotEmpty();

        final List<FilingFactResponse.FilingFact> filingFacts = factResponse
                .map(response -> response.facts().stream()
                        .toList())
                .orElse(List.of());

        assertThat(filingFacts).hasSize(1);
        assertThat(filingFacts.getFirst().concept()).isEqualTo(concept);
        assertThat(filingFacts.getFirst().observations()).hasSize(2);
    }

    @Test
    void testResolveOnAapl() throws IOException {
        final String contentAsString = aaplInstanceXml.getContentAsString(StandardCharsets.UTF_8);
        final String concept = "us-gaap:MarketableSecuritiesCurrent";

        final Optional<FilingFactResponse> factResponse = resolver.parseFiling(contentAsString, Set.of(Concept.of(concept)));

        assertThat(factResponse).isNotEmpty();

        final List<FilingFactResponse.FilingFact> filingFacts = factResponse
                .map(response -> response.facts().stream()
                        .toList())
                .orElse(List.of());


        assertThat(filingFacts).hasSize(1);
        assertThat(filingFacts.getFirst().concept()).isEqualTo(concept);
        assertThat(filingFacts.getFirst().observations()).hasSize(13);
    }

    @Test
    void testResolveOnOldAapl() throws IOException {
        final String contentAsString = aaplOldInstanceXml.getContentAsString(StandardCharsets.UTF_8);
        final String concept = "us-gaap:Revenues";

        final Optional<FilingFactResponse> factResponse = resolver.parseFiling(contentAsString, Set.of(Concept.of(concept)));

        assertThat(factResponse).isNotEmpty();
        final List<FilingFactResponse.FilingFact> filingFacts = factResponse
                .map(response -> response.facts().stream()
                        .toList())
                .orElse(List.of());

        assertThat(filingFacts).hasSize(1);
        assertThat(filingFacts.getFirst().concept()).isEqualTo(concept);
        assertThat(filingFacts.getFirst().observations()).hasSize(17);
    }

}
