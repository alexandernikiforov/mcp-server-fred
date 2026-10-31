package ch.alni.mcp.edgar.core;

import lombok.Getter;

@Getter
public enum FiscalYearCountType {
    ONE_YEAR(1),
    TWO_YEARS(2),
    THREE_YEARS(3),
    FOUR_YEARS(4),
    SIX_YEARS(6);

    private final int value;

    FiscalYearCountType(int value) {
        this.value = value;
    }
}
