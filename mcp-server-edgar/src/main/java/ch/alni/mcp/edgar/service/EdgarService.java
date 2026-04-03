package ch.alni.mcp.edgar.service;

public interface EdgarService {

    /**
     * Resolves the CIK (Central Index Key) for a given ticker symbol.
     *
     * @param ticker The ticker symbol for which to resolve the CIK.
     * @return The CIK associated with the given ticker symbol.
     */
    long resolveCik(String ticker);
}
