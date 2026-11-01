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
        }
        catch (ParserConfigurationException e) {
            throw new IllegalStateException("Cannot initialize DocumentBuilderFactory", e);
        }

        return factory;
    }
}
