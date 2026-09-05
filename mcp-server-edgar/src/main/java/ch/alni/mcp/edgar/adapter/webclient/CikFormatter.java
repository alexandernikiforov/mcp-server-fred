package ch.alni.mcp.edgar.adapter.webclient;


final class CikFormatter {
    private CikFormatter() {
    }

    /**
     * Formats a CIK number as a 10-digit string with leading zeros.
     *
     * @param cik the CIK number to format
     * @return the formatted CIK number
     */
    static String formatCik(long cik) {
        return String.format("%010d", cik);
    }
}
