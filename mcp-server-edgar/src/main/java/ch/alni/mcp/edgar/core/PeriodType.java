package ch.alni.mcp.edgar.core;


public enum PeriodType {
    INSTANT, DURATION;

    public static PeriodType fromString(String value) {
        return PeriodType.valueOf(value.toUpperCase());
    }

}
