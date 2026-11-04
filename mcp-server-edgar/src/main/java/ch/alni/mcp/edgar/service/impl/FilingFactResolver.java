package ch.alni.mcp.edgar.service.impl;

import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import java.io.StringReader;

@Component
class FilingFactResolver {

    private final XBRLInstanceParser instanceParser = new XBRLInstanceParser(
            DocumentBuilderFactories.documentBuilderFactory
    );

    XBRLInstanceParser.XBRLInstance parseFiling(String filingInstance) {
        return instanceParser.parseInstance(new InputSource(new StringReader(filingInstance)));
    }
}
