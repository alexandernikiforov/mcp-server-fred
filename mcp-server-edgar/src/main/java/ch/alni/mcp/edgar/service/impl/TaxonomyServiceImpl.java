package ch.alni.mcp.edgar.service.impl;

import ch.alni.mcp.edgar.core.Taxonomy;
import ch.alni.mcp.edgar.core.TaxonomyMetadata;
import ch.alni.mcp.edgar.service.TaxonomyService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class TaxonomyServiceImpl implements TaxonomyService {
    private final UsGaapConceptResolver usGaapConceptResolver;

    TaxonomyServiceImpl(UsGaapConceptResolver usGaapConceptResolver) {
        this.usGaapConceptResolver = usGaapConceptResolver;
    }

    @Override
    public Optional<TaxonomyMetadata> getConceptMetadata(Taxonomy taxonomy, String concept) {
        return switch (taxonomy) {
            case US_GAAP -> Optional.ofNullable(usGaapConceptResolver.getTaxonomyMetadata(concept));
        };
    }
}
