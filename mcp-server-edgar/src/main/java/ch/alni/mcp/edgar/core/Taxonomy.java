package ch.alni.mcp.edgar.core;

public enum Taxonomy {
    US_GAAP("us-gap");

    private final String value;

    Taxonomy(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
