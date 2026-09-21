package ch.alni.mcp.edgar.service.impl;

import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.slf4j.LoggerFactory.getLogger;

@Component
class UsGaapConceptResolver implements InitializingBean {
    private static final Logger LOG = getLogger(UsGaapConceptResolver.class);

    private static final String TAXONOMY_FILENAME = "us-gaap-2026.zip";
    private static final String XSD_FILENAME = "us-gaap-2026.xsd";
    private static final String DOC_FILENAME = "us-gaap-doc-2026.xml";
    private static final String LAB_FILENAME = "us-gaap-lab-2026.xml";

    @Override
    public void afterPropertiesSet() throws Exception {
        final XMLInputFactory xmlInputFactory = createXMLInputFactory();

        try (final InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(TAXONOMY_FILENAME);
             final ZipInputStream zipInputStream = new ZipInputStream(Objects.requireNonNull(resourceAsStream))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                switch (entry.getName()) {
                    case XSD_FILENAME:
                        break;
                    case DOC_FILENAME:
                        break;
                    case LAB_FILENAME:
                        break;
                    default:
                        LOG.warn("Unknown file {} in taxonomy: {}", TAXONOMY_FILENAME, entry.getName());
                }
            }
        }
    }

    private void processDoc(final XMLInputFactory xmlInputFactory, final ZipInputStream zipInputStream) throws XMLStreamException {
        final XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(zipInputStream);

        try {
            while (reader.hasNext()) {
                final int event = reader.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    final String elementName = reader.getLocalName();

                    if ("item".equals(elementName)) {
                        String id = reader.getAttributeValue(null, "id");
                        System.out.println("Item id: " + id);
                    }
                }

                if (event == XMLStreamConstants.CHARACTERS) {
                    String text = reader.getText().trim();

                    if (!text.isEmpty()) {
                        System.out.println("Text: " + text);
                    }
                }
            }
        }
        finally {
            reader.close();
        }
    }

    private void processLab(final XMLInputFactory xmlInputFactory, final ZipInputStream zipInputStream) {

    }

    private void processXsd(final XMLInputFactory xmlInputFactory, final ZipInputStream zipInputStream) {

    }

    private XMLInputFactory createXMLInputFactory() {
        XMLInputFactory factory = XMLInputFactory.newFactory();

        // Security: prevent XXE and external entity attacks
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        return factory;
    }
}
