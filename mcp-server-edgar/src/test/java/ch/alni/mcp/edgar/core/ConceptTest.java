package ch.alni.mcp.edgar.core;

import ch.alni.mcp.edgar.service.Concept;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConceptTest {

    @Test
    void testRecordCreation() {
        assertThat(new Concept("US-GAAP", "Revenues")).isEqualTo(Concept.of("us-gaap:Revenues"));
    }
}
