package ch.alni.mcp.edgar.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = {
        FilingFactResolver.class
})
class FilingFactResolverTest {

    @Value("classpath:/responses/baba-20260331_htm.xml")
    private Resource instanceXml;

    @Autowired
    private FilingFactResolver resolver;

    @Test
    void testResolve() throws IOException {
        final String contentAsString = instanceXml.getContentAsString(StandardCharsets.UTF_8);
        final XBRLInstanceParser.XBRLInstance instance = resolver.parseFiling(contentAsString);

        assertThat(instance).isNotNull();
    }
}