package ch.alni.mcp.edgar.service.impl;

import ch.alni.mcp.edgar.core.TaxonomyConcept;
import ch.alni.mcp.edgar.core.TaxonomyLabel;
import ch.alni.mcp.edgar.service.TaxonomyMetadata;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
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

    private final Map<String, TaxonomyConcept> taxonomyConcepts = new HashMap<>();
    private final Map<String, TaxonomyLabel> taxonomyLabels = new HashMap<>();
    @Value("classpath:us-gaap-2026.zip")
    private Resource taxonomyResource;

    private UsGaapConceptResolver() {
    }

    public @Nullable TaxonomyMetadata getTaxonomyMetadata(String conceptKey) {
        final TaxonomyConcept concept = this.taxonomyConcepts.get(conceptKey);
        if (concept == null) {
            return null;
        }
        final TaxonomyLabel label = this.taxonomyLabels.get(concept.id());
        return TaxonomyMetadata.builder()
                .tag(concept.name())
                .balance(concept.balance())
                .periodType(concept.periodType())
                .dataType(concept.type())
                .documentation(label.text())
                .build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final XBRLConceptParser parser = new XBRLConceptParser(DocumentBuilderFactories.documentBuilderFactory);

        try (final InputStream resourceAsStream = taxonomyResource.getInputStream();
             final ZipInputStream zipInputStream = new ZipInputStream(resourceAsStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                switch (entry.getName()) {
                    case XSD_FILENAME:
                        this.taxonomyConcepts.putAll(parser.parseConceptsXsd(zipInputStream));
                        break;
                    case DOC_FILENAME:
                        this.taxonomyLabels.putAll(parser.parseDocs(zipInputStream));
                        break;
                    case LAB_FILENAME:
                        break;
                    default:
                        LOG.warn("Unknown file {} in taxonomy: {}", TAXONOMY_FILENAME, entry.getName());
                }
            }
        }
    }

}
