package ch.alni.mcp.edgar.service.impl;

import ch.alni.mcp.edgar.core.Concept;
import ch.alni.mcp.edgar.service.FilingFactResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = {
        FilingFactResolver.class
})
class FilingFactResolverTest {

    @Value("classpath:/responses/baba-20260331_htm.xml")
    private Resource instanceXml;

    @Autowired
    private FilingFactResolver resolver;

    @Test
    void testResolve() throws IOException {
        final String contentAsString = instanceXml.getContentAsString(StandardCharsets.UTF_8);
        final String concept = "baba:PaymentsToAcquireLandUseRightsPropertyAndEquipment";

        final FilingFactResponse factResponse = resolver.parseFiling(contentAsString, Set.of(Concept.of(concept)));

        assertThat(factResponse).isNotNull();
        final List<FilingFactResponse.FilingFact> filingFacts = factResponse.facts()
                .stream()
                .toList();

        assertThat(filingFacts).hasSize(1);
        assertThat(filingFacts.getFirst().concept()).isEqualTo(concept);
        assertThat(filingFacts.getFirst().observations()).hasSize(2);
    }
}
