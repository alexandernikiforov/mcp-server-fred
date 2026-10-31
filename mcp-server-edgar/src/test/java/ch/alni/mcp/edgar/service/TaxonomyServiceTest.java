package ch.alni.mcp.edgar.service;

import ch.alni.mcp.edgar.core.BalanceType;
import ch.alni.mcp.edgar.core.PeriodType;
import ch.alni.mcp.edgar.core.Taxonomy;
import ch.alni.mcp.edgar.core.TaxonomyMetadata;
import ch.alni.mcp.edgar.port.CompanyConceptResponse;
import ch.alni.mcp.edgar.port.CompanyFact;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import tools.jackson.databind.json.JsonMapper;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

class TaxonomyServiceTest extends EdgarServiceTestSupport {
    private static final Logger LOG = getLogger(TaxonomyServiceTest.class);

    @Autowired
    private TaxonomyService taxonomyService;

    @Autowired
    private JsonMapper jsonMapper;

    @Value("classpath:responses/CIK0000320193-concept-us-gaap-netincomeloss.json")
    private Resource netIncomeLoss;

    @Value("classpath:responses/CIK0000320193-concept-us-gaap-dilitutedshares.json")
    private Resource dilutedShares;

    @Test
    void getConceptMetadata() {
        assertThat(taxonomyService.getConceptMetadata(Taxonomy.US_GAAP, "RightOfUseAssetObtainedInExchangeForFinanceLeaseLiability"))
                .contains(TaxonomyMetadata.builder()
                        .tag("RightOfUseAssetObtainedInExchangeForFinanceLeaseLiability")
                        .dataType("xbrli:monetaryItemType")
                        .balance(BalanceType.DEBIT)
                        .periodType(PeriodType.DURATION)
                        .documentation("Amount of increase in right-of-use asset obtained in exchange for finance lease liability.")
                        .build());

        assertThat(taxonomyService.getConceptMetadata(Taxonomy.US_GAAP, "Bla")).isEmpty();
    }

    @Test
    void testFacts() throws Exception {
        try (InputStream inputStream = netIncomeLoss.getInputStream()) {
            final CompanyConceptResponse response = jsonMapper.readValue(inputStream, CompanyConceptResponse.class);
            assertThat(response).isNotNull();

            final List<CompanyFact> companyFacts = response.units().get("USD");
            companyFacts.sort(Comparator.comparing(CompanyFact::filed));
            companyFacts.forEach(System.out::println);
        }
    }
}
