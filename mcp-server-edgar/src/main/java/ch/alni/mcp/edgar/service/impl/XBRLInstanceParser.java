package ch.alni.mcp.edgar.service.impl;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

public class XBRLInstanceParser {
    private static final Logger LOG = getLogger(XBRLInstanceParser.class);

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
        }
        catch (NumberFormatException e) {
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

    private static QName resolveQName(Element element) {
        final String value = element.getTextContent().strip();
        final String qualifiedName = value.trim();
        final int separator = qualifiedName.indexOf(':');

        final String prefix = separator < 0
                ? ""
                : qualifiedName.substring(0, separator);

        final String localName = separator < 0
                ? qualifiedName
                : qualifiedName.substring(separator + 1);

        final String namespaceUri = element.lookupNamespaceURI(prefix.isEmpty() ? null : prefix);
        if (namespaceUri == null) {
            return new QName(XMLConstants.NULL_NS_URI, localName);
        }
        else {
            return new QName(namespaceUri, localName, prefix);
        }
    }

    private LocalDate getDocumentPeriodEndDate(Document document) {
        final String namespaceUri = document.lookupNamespaceURI("dei");
        final NodeList elements = document.getElementsByTagNameNS(namespaceUri, "DocumentPeriodEndDate");
        if (elements.getLength() == 0) {
            return null;
        }
        final Element element = (Element) elements.item(0);
        final String value = element.getTextContent();
        return LocalDate.parse(value);
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
        return Collections.unmodifiableMap(contexts);
    }

    private boolean isXBRLInstance(Document document) {
        final Element root = document.getDocumentElement();

        return root != null
                && "xbrl".equals(root.getLocalName())
                && XBRLNamespaces.XBRL_INSTANCE_NS.equals(root.getNamespaceURI());
    }

    public Optional<XBRLInstance> parseXBRLInstance(InputSource inputSource) {
        try {
            final DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            final Document document = builder.parse(inputSource);

            if (!isXBRLInstance(document)) {
                return Optional.empty();
            }

            final List<XBRLInstance.Fact> facts = getFacts(document);
            final Map<String, XBRLInstance.Context> contexts = getContextMap(document);
            final Map<String, XBRLInstance.Unit> units = getUnitMap(document);
            final LocalDate documentPeriodEndDate = getDocumentPeriodEndDate(document);
            return Optional.of(new XBRLInstance(documentPeriodEndDate, contexts, units, facts));
        }
        catch (ParserConfigurationException | SAXException | IOException e) {
            LOG.error("Cannot parse content as an XML file", e);
            return Optional.empty();
        }
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
        return Collections.unmodifiableMap(dimensions);
    }

    private Map<String, XBRLInstance.Unit> getUnitMap(Document document) {
        final Map<String, XBRLInstance.Unit> units = new HashMap<>();
        final NodeList elements = document.getElementsByTagNameNS(XBRLNamespaces.XBRL_INSTANCE_NS, "unit");
        for (int i = 0; i < elements.getLength(); i++) {
            final Element element = (Element) elements.item(i);

            final String id = element.getAttributeNS(null, "id");

            final Element divide = (Element) element
                    .getElementsByTagNameNS(XBRLNamespaces.XBRL_INSTANCE_NS, "divide")
                    .item(0);

            if (divide == null) {
                units.put(id, new XBRLInstance.Unit(id, getMeasures(element), List.of()));

            }
            else {
                final NodeList numerators = divide
                        .getElementsByTagNameNS(XBRLNamespaces.XBRL_INSTANCE_NS, "unitNumerator");

                final List<QName> numeratorQNames = new ArrayList<>();
                for (int j = 0; j < numerators.getLength(); j++) {
                    final Element numerator = (Element) numerators.item(j);
                    numeratorQNames.addAll(getMeasures(numerator));
                }

                final NodeList denominators = divide
                        .getElementsByTagNameNS(XBRLNamespaces.XBRL_INSTANCE_NS, "unitDenominator");

                final List<QName> denominatorQNames = new ArrayList<>();
                for (int j = 0; j < denominators.getLength(); j++) {
                    final Element denominator = (Element) denominators.item(j);
                    denominatorQNames.addAll(getMeasures(denominator));
                }
                units.put(id, new XBRLInstance.Unit(id, numeratorQNames, denominatorQNames));
            }
        }
        return Collections.unmodifiableMap(units);
    }

    private List<QName> getMeasures(Element parent) {
        final NodeList measures = parent.getElementsByTagNameNS(XBRLNamespaces.XBRL_INSTANCE_NS, "measure");
        final List<QName> qnames = new ArrayList<>();
        for (int j = 0; j < measures.getLength(); j++) {
            final Element measure = (Element) measures.item(j);
            qnames.add(resolveQName(measure));
        }
        return qnames;
    }

    /**
     * XBRLInstance as a parsed structure.
     *
     * @param documentPeriodEndDate
     * @param contexts              contexts mapped by their IDs
     * @param facts                 facts
     */
    public record XBRLInstance(
            LocalDate documentPeriodEndDate, Map<String, Context> contexts,
            Map<String, Unit> units,
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

        public record Unit(
                String id,
                List<QName> numerator,
                List<QName> denominator
        ) {
        }
    }
}
