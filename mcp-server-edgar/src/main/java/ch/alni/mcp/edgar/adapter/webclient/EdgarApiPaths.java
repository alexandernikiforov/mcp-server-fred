package ch.alni.mcp.edgar.adapter.webclient;

/**
 * TODO: javadoc
 */
public class EdgarApiPaths {
    static final String ACCESSION_PATH = "/accession/{cik}/{accession}/{primaryDocument}";
    static final String SUBMISSIONS_PATH = "/submissions/CIK{cik}.json";
    static final String COMPANY_FACTS_PATH = "/api/xbrl/companyfacts/CIK{cik}.json";
    static final String COMPANY_CONCEPT_PATH = "/api/xbrl/companyconcept/CIK{cik}/{taxonomy}/{concept}.json";
}
