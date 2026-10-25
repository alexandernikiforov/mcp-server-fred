package ch.alni.mcp.edgar.service;

import ch.alni.mcp.edgar.core.BalanceType;
import ch.alni.mcp.edgar.core.PeriodType;
import ch.alni.mcp.edgar.core.Taxonomy;
import ch.alni.mcp.edgar.core.TaxonomyMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class TaxonomyServiceTest extends EdgarServiceTestSupport {

    @Autowired
    private TaxonomyService taxonomyService;

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
}
