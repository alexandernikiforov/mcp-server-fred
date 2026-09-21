package ch.alni.mcp.edgar.service.impl;

import ch.alni.mcp.edgar.core.Taxonomy;
import ch.alni.mcp.edgar.service.ConceptMetadata;
import ch.alni.mcp.edgar.service.TaxonomyService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
class TaxonomyServiceImpl implements TaxonomyService {

    @Override
    public Mono<ConceptMetadata> getConceptMetadata(Taxonomy taxonomy, String concept) {
        return null;
    }
}
