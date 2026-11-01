package ch.alni.mcp.edgar.service;

import lombok.Builder;

import java.util.List;

@Builder
public record FilingStructure(List<Section> sections) {

    @Builder
    public record Section(
            String role,
            String definition,
            List<ConceptNode> nodes
    ) {
    }

    @Builder
    public record ConceptNode(
            String concept,
            List<ConceptNode> children
    ) {
    }

}
