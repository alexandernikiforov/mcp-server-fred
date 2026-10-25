package ch.alni.mcp.edgar.service.impl;

import ch.alni.mcp.edgar.core.BalanceType;
import ch.alni.mcp.edgar.core.PeriodType;
import ch.alni.mcp.edgar.core.TaxonomyConcept;
import ch.alni.mcp.edgar.core.TaxonomyLabel;
import org.jspecify.annotations.NonNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: javadoc
 */
public class XBRLConceptParser {

    public static final String XBRL_INSTANCE_NS = "http://www.xbrl.org/2003/instance";
    private static final String XBRL_LINK_NS = "http://www.xbrl.org/2003/linkbase";
    private static final String XLINK_NS = "http://www.w3.org/1999/xlink";
    private final DocumentBuilderFactory documentBuilderFactory;

    public XBRLConceptParser(DocumentBuilderFactory documentBuilderFactory) {
        this.documentBuilderFactory = documentBuilderFactory;
    }

    private static Map<String, String> readLocators(NodeList locators) {
        final Map<String, String> result = new HashMap<>();
        for (int i = 0; i < locators.getLength(); i++) {
            final Element locator = (Element) locators.item(i);
            final String href = locator.getAttributeNS(XLINK_NS, "href");
            final String label = locator.getAttributeNS(XLINK_NS, "label");
            result.put(label, href);
        }
        return Collections.unmodifiableMap(result);
    }

    private static Map<String, String> readResources(NodeList resources) {
        final Map<String, String> result = new HashMap<>();
        for (int i = 0; i < resources.getLength(); i++) {
            final Element resource = (Element) resources.item(i);
            final String label = resource.getAttributeNS(XLINK_NS, "label");
            final String text = resource.getTextContent();
            result.put(label, text);
        }
        return Collections.unmodifiableMap(result);
    }

    private static @NonNull InputStream createNonClosingStream(InputStream inputStream) {
        return new FilterInputStream(inputStream) {
            @Override
            public void close() {
                // The outer try-with-resources owns the ZIP stream.
            }
        };
    }

    public Map<String, TaxonomyLabel> parseDocs(InputStream inputStream) throws IOException {
        final InputStream nonClosingInput = createNonClosingStream(inputStream);
        final Map<String, TaxonomyLabel> taxonomyLabels = new HashMap<>();

        try {
            final DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            final Document document = builder.parse(nonClosingInput);
            final Map<String, String> locators = readLocators(
                    document.getElementsByTagNameNS(XBRL_LINK_NS, "loc")
            );

            final Map<String, String> resources = readResources(
                    document.getElementsByTagNameNS(XBRL_LINK_NS, "label")
            );

            final NodeList arcs = document.getElementsByTagNameNS(XBRL_LINK_NS, "labelArc");
            for (int i = 0; i < arcs.getLength(); i++) {
                final Element arc = (Element) arcs.item(i);
                final String fromLocator = arc.getAttributeNS(XLINK_NS, "from");
                final String toLabel = arc.getAttributeNS(XLINK_NS, "to");

                final String href = locators.get(fromLocator);
                final String label = resources.get(toLabel);
                final String id = href.substring(href.indexOf('#') + 1);

                taxonomyLabels.put(id, new TaxonomyLabel(id, label));
            }

            return Collections.unmodifiableMap(taxonomyLabels);
        }
        catch (ParserConfigurationException | SAXException e) {
            throw new IllegalStateException("Cannot parse XBRL docs stream", e);
        }
    }

    public Map<String, TaxonomyConcept> parseConceptsXsd(InputStream inputStream) throws IOException {
        final InputStream nonClosingInput = createNonClosingStream(inputStream);

        final Map<String, TaxonomyConcept> concepts = new HashMap<>();
        try {
            final DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            final Document document = builder.parse(nonClosingInput);

            final NodeList elements = document.getElementsByTagNameNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
            for (int i = 0; i < elements.getLength(); i++) {
                final Element e = (Element) elements.item(i);
                boolean isAbstract = Boolean.parseBoolean(e.getAttribute("abstract"));
                if (isAbstract) {
                    // skip abstract concepts that are not representable
                    continue;
                }

                final String substitutionGroup = e.getAttribute("substitutionGroup");
                if (substitutionGroup.contains("dimensionItem")) {
                    continue;
                }

                final String id = e.getAttribute("id");
                if (id.contains(".domain")) {
                    // skip domain concepts
                    continue;
                }

                final String name = e.getAttribute("name");
                final String type = e.getAttribute("type");
                final String periodType = e.getAttributeNS(XBRL_INSTANCE_NS, "periodType");
                final String balance = e.getAttributeNS(XBRL_INSTANCE_NS, "balance");
                concepts.put(name, TaxonomyConcept.builder()
                        .id(id)
                        .name(name)
                        .type(type)
                        .periodType(PeriodType.fromString(periodType))
                        .balance(BalanceType.fromString(balance))
                        .build());
            }

            return Collections.unmodifiableMap(concepts);
        }
        catch (ParserConfigurationException | SAXException e) {
            throw new IllegalStateException("Cannot parse XBRL concept stream", e);
        }

    }

}
