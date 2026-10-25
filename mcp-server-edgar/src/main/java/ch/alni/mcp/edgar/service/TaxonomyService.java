package ch.alni.mcp.edgar.service;

import ch.alni.mcp.edgar.core.Taxonomy;
import ch.alni.mcp.edgar.core.TaxonomyMetadata;

import java.util.Optional;

/**
 * TODO: javadoc
 */
public interface TaxonomyService {

    Optional<TaxonomyMetadata> getConceptMetadata(Taxonomy taxonomy, String tag);
}
