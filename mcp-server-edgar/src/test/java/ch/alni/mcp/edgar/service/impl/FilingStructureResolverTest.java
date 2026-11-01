package ch.alni.mcp.edgar.service.impl;

import ch.alni.mcp.edgar.service.FilingStructure;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = {
        FilingStructureResolver.class
})
class FilingStructureResolverTest {

    @Value("classpath:/responses/aapl-20260627.xsd")
    private Resource companyXsd;

    @Value("classpath:/responses/aapl-20260627_pre.xml")
    private Resource preXml;

    @Autowired
    private FilingStructureResolver resolver;

    @Test
    void resolve() throws Exception {
        final String xsd = companyXsd.getContentAsString(StandardCharsets.UTF_8);
        final String pre = preXml.getContentAsString(StandardCharsets.UTF_8);
        final FilingStructure filingStructure = resolver.resolve(xsd, pre);

        assertThat(filingStructure).isNotNull();
    }
}
