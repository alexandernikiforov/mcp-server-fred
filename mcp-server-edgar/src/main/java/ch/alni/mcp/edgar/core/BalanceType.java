package ch.alni.mcp.edgar.core;


public enum BalanceType {
    DEBIT,
    CREDIT,
    NONE;

    public static BalanceType fromString(String value) {
        if (null == value || value.isBlank()) {
            return BalanceType.NONE;
        }

        return BalanceType.valueOf(value.toUpperCase());
    }
}
