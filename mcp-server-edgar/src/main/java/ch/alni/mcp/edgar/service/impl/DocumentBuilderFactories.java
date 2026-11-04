package ch.alni.mcp.edgar.service.impl;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

final class DocumentBuilderFactories {

    static final DocumentBuilderFactory documentBuilderFactory = createDocumentBuilderFactory();

    private DocumentBuilderFactories() {
    }

    private static DocumentBuilderFactory createDocumentBuilderFactory() {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Security hardening
        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            factory.setNamespaceAware(true);

            factory.setAttribute("jdk.xml.maxGeneralEntitySizeLimit", "10000000");
            factory.setAttribute("jdk.xml.totalEntitySizeLimit", "25000000");

            // Keep external resource access disabled.
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        }
        catch (ParserConfigurationException e) {
            throw new IllegalStateException("Cannot initialize DocumentBuilderFactory", e);
        }

        return factory;
    }
}
