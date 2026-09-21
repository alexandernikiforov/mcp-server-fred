package ch.alni.mcp.edgar.service;

import ch.alni.mcp.edgar.core.Taxonomy;
import reactor.core.publisher.Mono;

/**
 * TODO: javadoc
 */
public interface TaxonomyService {

    Mono<ConceptMetadata> getConceptMetadata(Taxonomy taxonomy, String concept);
}
