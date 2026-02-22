package ch.alni.mcp.fred.port;

/**
 * File types supported by the FRED server.
 */
public enum FileType {

    JSON("json"),
    XML("xml"),
    XLSX("xlsx"),
    CSV_ZIPPED("csv");

    private final String value;

    FileType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
