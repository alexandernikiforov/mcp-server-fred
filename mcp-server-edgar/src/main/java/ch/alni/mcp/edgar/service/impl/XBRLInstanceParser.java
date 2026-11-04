package ch.alni.mcp.edgar.service.impl;

import org.jspecify.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XBRLInstanceParser {

    private final DocumentBuilderFactory documentBuilderFactory;

    public XBRLInstanceParser(DocumentBuilderFactory documentBuilderFactory) {
        this.documentBuilderFactory = documentBuilderFactory;
    }

    private static BigDecimal getNumericalValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return new BigDecimal(value.strip());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static @Nullable LocalDate readDate(Element period, String namespaceUri, String localName) {
        final NodeList dates = period.getElementsByTagNameNS(namespaceUri, localName);
        if (dates.getLength() == 0) {
            return null;
        }

        return LocalDate.parse(dates.item(0).getTextContent().strip());
    }

    public XBRLInstance parseInstance(InputSource inputSource) {
        try {
            final DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            final Document document = builder.parse(inputSource);
            final List<XBRLInstance.Fact> facts = getFacts(document);
            final Map<String, XBRLInstance.Context> contexts = getContextMap(document);
            return new XBRLInstance(contexts, facts);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalStateException("Cannot parse company XSD stream", e);
        }
    }

    private List<XBRLInstance.Fact> getFacts(Document document) {
        // go over all elements
        final List<XBRLInstance.Fact> facts = new ArrayList<>();
        final NodeList elements = document.getElementsByTagNameNS("*", "*");

        for (int i = 0; i < elements.getLength(); i++) {
            final Element element = (Element) elements.item(i);

            final String nil = element.getAttributeNS(
                    XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "nil"
            );

            final boolean isNil = "true".equals(nil.strip()) || "1".equals(nil.strip());
            if (isNil) {
                // skip nil elements
                continue;
            }

            if (!element.hasAttributeNS(null, "contextRef")) {
                // skip if the context is not present
                continue;
            }

            if (!element.hasAttributeNS(null, "unitRef")) {
                // skip if the context is not present
                continue;
            }

            final String valueAsString = element.getTextContent().strip();
            final BigDecimal value = getNumericalValue(valueAsString);
            if (value == null) {
                // skip if the value is not numerical or not present
                continue;
            }

            final String unitRef = element.getAttributeNS(null, "unitRef");
            final String qualifiedName = element.getTagName();
            final String contextRef = element.getAttributeNS(null, "contextRef");
            final String id = element.getAttributeNS(null, "id");

            facts.add(new XBRLInstance.Fact(
                    id,
                    qualifiedName,
                    contextRef,
                    unitRef,
                    value
            ));
        }
        return facts;
    }

    private Map<String, XBRLInstance.Context> getContextMap(Document document) {
        final Map<String, XBRLInstance.Context> contexts = new HashMap<>();
        final NodeList elements = document.getElementsByTagNameNS(XBRLNamespaces.XBRL_INSTANCE_NS, "context");
        for (int i = 0; i < elements.getLength(); i++) {
            final Element element = (Element) elements.item(i);

            final Element period = (Element) element
                    .getElementsByTagNameNS(XBRLNamespaces.XBRL_INSTANCE_NS, "period")
                    .item(0);

            if (period == null) {
                // this is invalid context, skip it
                continue;
            }

            final String id = element.getAttributeNS(null, "id");
            final LocalDate instant = readDate(period, XBRLNamespaces.XBRL_INSTANCE_NS, "instant");
            final LocalDate startDate = readDate(period, XBRLNamespaces.XBRL_INSTANCE_NS, "startDate");
            final LocalDate endDate = readDate(period, XBRLNamespaces.XBRL_INSTANCE_NS, "endDate");

            final Map<String, String> dimensions = getDimensions(element);
            final XBRLInstance.Context context = new XBRLInstance.Context(
                    id,
                    startDate,
                    endDate,
                    instant,
                    dimensions
            );

            contexts.put(id, context);
        }
        return contexts;
    }

    private Map<String, String> getDimensions(Element context) {
        final Map<String, String> dimensions = new HashMap<>();
        final NodeList members = context.getElementsByTagNameNS(
                XBRLNamespaces.XBRL_DIMENSIONS_NS, "explicitMember"
        );

        for (int j = 0; j < members.getLength(); j++) {
            final Element member = (Element) members.item(j);
            final String dimension = member.getAttributeNS(null, "dimension");
            final String memberName = member.getTextContent().strip();

            dimensions.put(dimension, memberName);
        }
        return dimensions;
    }

    /**
     * XBRLInstance as a parsed structure.
     *
     * @param contexts contexts mapped by their IDs
     * @param facts    facts
     */
    public record XBRLInstance(
            Map<String, Context> contexts,
            List<Fact> facts
    ) {
        /**
         * @param id         fact ID
         * @param concept    the concept, for example "us-gaap:Assets"
         * @param contextRef context ID reference
         * @param unitRef    unit ID reference
         * @param value      provided value
         */
        public record Fact(
                String id,
                String concept,
                String contextRef,
                String unitRef,
                BigDecimal value
        ) {
        }

        /**
         * Representation of a context in an XBRL instance.
         *
         * @param id         ID of the context
         * @param startDate  Start date of the context, not null context relates to a period
         * @param endDate    End date of the context, not null context relates to a period
         * @param instant    Instant of the context, not null context relates to a point in time
         * @param dimensions Dimensions of the context
         */
        public record Context(
                String id,
                @Nullable LocalDate startDate,
                @Nullable LocalDate endDate,
                @Nullable LocalDate instant,
                Map<String, String> dimensions
        ) {
        }
    }
}