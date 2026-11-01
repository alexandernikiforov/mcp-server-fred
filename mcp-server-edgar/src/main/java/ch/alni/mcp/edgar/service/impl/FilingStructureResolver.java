package ch.alni.mcp.edgar.service.impl;

import ch.alni.mcp.edgar.service.FilingStructure;
import ch.alni.mcp.edgar.service.impl.CompanyPresentationParser.Arc;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
class FilingStructureResolver {

    private final CompanyPresentationParser parser = new CompanyPresentationParser(
            DocumentBuilderFactories.documentBuilderFactory
    );

    FilingStructure resolve(String companyXsd, String preXml) {
        final var roles = parser.parseCompanyXsd(new InputSource(new StringReader(companyXsd)));
        final var arcsByRole = parser.parsePresentationXml(new InputSource(new StringReader(preXml)));

        final var sections = roles.entrySet().stream()
                .map(entry -> new FilingStructure.Section(
                        entry.getKey(),
                        entry.getValue(),
                        toConceptNodes(arcsByRole.getOrDefault(entry.getKey(), List.of()))
                ))
                .toList();

        return new FilingStructure(sections);
    }

    private List<FilingStructure.ConceptNode> toConceptNodes(List<Arc> arcs) {
        final Map<String, List<String>> childrenByConcept = arcs.stream()
                .collect(Collectors.groupingBy(
                        Arc::fromConcept,
                        LinkedHashMap::new,
                        Collectors.mapping(Arc::toConcept, Collectors.toList())
                ));

        final Set<String> childConcepts = arcs.stream()
                .map(Arc::toConcept)
                .collect(Collectors.toSet());

        return childrenByConcept.keySet().stream()
                .filter(concept -> !childConcepts.contains(concept))
                .map(concept -> toConceptNode(concept, childrenByConcept, new HashSet<>()))
                .toList();
    }

    private FilingStructure.ConceptNode toConceptNode(
            String concept,
            Map<String, List<String>> childrenByConcept,
            Set<String> path
    ) {
        path.add(concept);

        final var children = childrenByConcept.getOrDefault(concept, List.of()).stream()
                .filter(child -> !path.contains(child))
                .map(child -> toConceptNode(child, childrenByConcept, path))
                .toList();

        path.remove(concept);
        return new FilingStructure.ConceptNode(concept, children);
    }
}
