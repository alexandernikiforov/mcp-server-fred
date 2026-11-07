package ch.alni.mcp.edgar.service.impl;

import ch.alni.mcp.edgar.service.Concept;
import ch.alni.mcp.edgar.service.FilingFactResponse;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.Optional;
import java.util.Set;

@Component
class FilingFactResolver {

    private final XBRLInstanceParser instanceParser = new XBRLInstanceParser(
            DocumentBuilderFactories.documentBuilderFactory
    );

    Optional<FilingFactResponse> parseFiling(String filingInstance, Set<Concept> concepts) {
        return instanceParser.parseXBRLInstance(new InputSource(new StringReader(filingInstance)))
                .map(xbrlInstance -> FilingFactConverter.toFilingFacts(xbrlInstance, concepts))
                .map(FilingFactResponse::new);
    }
}
