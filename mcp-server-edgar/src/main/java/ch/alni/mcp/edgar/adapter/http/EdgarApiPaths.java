package ch.alni.mcp.edgar.adapter.http;

public final class EdgarApiPaths {
    public static final String ACCESSION_PATH = "/accession/{cik}/{accession}/{primaryDocument}";
    public static final String SUBMISSIONS_PATH = "/submissions/CIK{cik}.json";
    public static final String COMPANY_FACTS_PATH = "/api/xbrl/companyfacts/CIK{cik}.json";
    public static final String COMPANY_CONCEPT_PATH = "/api/xbrl/companyconcept/CIK{cik}/{taxonomy}/{concept}.json";

    private EdgarApiPaths() {
    }
}
