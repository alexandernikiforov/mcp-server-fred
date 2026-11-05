package ch.alni.mcp.edgar.service.impl;

import ch.alni.mcp.edgar.service.FilingFactResponse;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.List;
import java.util.Set;

@Component
class FilingFactResolver {

    private final XBRLInstanceParser instanceParser = new XBRLInstanceParser(
            DocumentBuilderFactories.documentBuilderFactory
    );

    FilingFactResponse parseFiling(String filingInstance, Set<String> concepts) {
        final XBRLInstanceParser.XBRLInstance xbrlInstance = instanceParser.parseInstance(
                new InputSource(new StringReader(filingInstance))
        );
        final List<FilingFactResponse.FilingFact> filingFacts = FilingFactConverter.toFilingFacts(xbrlInstance, concepts);
        return new FilingFactResponse(filingFacts);
    }
}
