package ch.alni.mcp.edgar.service.impl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyPresentationParser {
    private final DocumentBuilderFactory documentBuilderFactory;

    public CompanyPresentationParser(DocumentBuilderFactory documentBuilderFactory) {
        this.documentBuilderFactory = documentBuilderFactory;
    }

    /**
     * Parses a company XSD InputSource to extract roleType elements and their definitions. Extracted roles are mapped
     * by their roleURI and returned as an unmodifiable map.
     *
     * @param inputSource an InputSource representing the company XSD file to be parsed
     * @return a map where each key is a roleURI, and the corresponding value is its definition
     */
    public Map<String, String> parseCompanyXsd(InputSource inputSource) {
        try {
            final DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            final Document document = builder.parse(inputSource);

            final Map<String, String> roleMap = new HashMap<>();
            final NodeList roles = document.getElementsByTagNameNS(XBRLNamespaces.XBRL_LINK_NS, "roleType");
            for (int i = 0; i < roles.getLength(); i++) {
                final Element role = (Element) roles.item(i);

                final NodeList definitions = role.getElementsByTagNameNS(XBRLNamespaces.XBRL_LINK_NS, "definition");
                if (definitions.getLength() == 0) {
                    continue;
                }

                final String roleURI = role.getAttribute("roleURI");
                final String definition = definitions.item(0).getTextContent().strip();
                roleMap.put(roleURI, definition);
            }

            return Collections.unmodifiableMap(roleMap);
        }
        catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalStateException("Cannot parse company XSD stream", e);
        }
    }

    /**
     * Parses the given XML InputSource to extract presentation link definitions, including roles, locators, and arcs.
     * The data is structured as a map where each role is associated with a list of arcs. Each arc represents a
     * relationship between concepts.
     *
     * @param inputSource the InputSource representing the XML document to be parsed
     * @return a map where the keys are roles (as strings) and the values are lists of Arc objects, each representing a
     * relationship between concepts
     */
    public Map<String, List<Arc>> parsePresentationXml(InputSource inputSource) {
        try {
            final DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            final Document document = builder.parse(inputSource);

            final Map<String, List<Arc>> roleMap = new HashMap<>();
            final NodeList links = document.getElementsByTagNameNS(XBRLNamespaces.XBRL_LINK_NS, "presentationLink");
            for (int i = 0; i < links.getLength(); i++) {
                final Element linkElement = (Element) links.item(i);

                final String role = linkElement.getAttributeNS(XBRLNamespaces.XLINK_NS, "role");

                final NodeList locators = linkElement.getElementsByTagNameNS(XBRLNamespaces.XBRL_LINK_NS, "loc");
                final Map<String, String> locatorMap = readLocators(locators);

                final List<Arc> arcs = new ArrayList<>();
                final NodeList preArcs = linkElement.getElementsByTagNameNS(XBRLNamespaces.XBRL_LINK_NS, "presentationArc");
                for (int j = 0; j < preArcs.getLength(); j++) {
                    final Element preArc = (Element) preArcs.item(j);
                    final String from = preArc.getAttributeNS(XBRLNamespaces.XLINK_NS, "from");
                    final String to = preArc.getAttributeNS(XBRLNamespaces.XLINK_NS, "to");

                    final String fromConcept = locatorMap.get(from);
                    final String toConcept = locatorMap.get(to);
                    arcs.add(new Arc(fromConcept, toConcept));
                }

                roleMap.put(role, arcs);
            }

            return Collections.unmodifiableMap(roleMap);
        }
        catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalStateException("Cannot parse company XSD stream", e);
        }
    }

    private Map<String, String> readLocators(NodeList locators) {
        final Map<String, String> locatorMap = new HashMap<>();
        for (int i = 0; i < locators.getLength(); i++) {
            final Element locator = (Element) locators.item(i);
            final String label = locator.getAttributeNS(XBRLNamespaces.XLINK_NS, "label");
            final String href = locator.getAttributeNS(XBRLNamespaces.XLINK_NS, "href");
            locatorMap.put(label, href);
        }
        return locatorMap;
    }

    public record Arc(String fromConcept, String toConcept) {
    }
}
