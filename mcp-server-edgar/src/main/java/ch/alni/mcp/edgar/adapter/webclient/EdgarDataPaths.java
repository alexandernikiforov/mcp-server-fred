package ch.alni.mcp.edgar.adapter.webclient;

public final class EdgarDataPaths {
    public static final String SUBMISSIONS_PATH = "/submissions/CIK{cik}.json";
    public static final String COMPANY_FACTS_PATH = "/api/xbrl/companyfacts/CIK{cik}.json";
    public static final String COMPANY_CONCEPT_PATH = "/api/xbrl/companyconcept/CIK{cik}/{taxonomy}/{concept}.json";

    private EdgarDataPaths() {
    }
}
