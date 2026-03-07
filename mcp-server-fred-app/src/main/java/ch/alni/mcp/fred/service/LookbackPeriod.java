package ch.alni.mcp.fred.service;

import lombok.Getter;

import java.time.Period;

@Getter
public enum LookbackPeriod {
    LAST_1Y(Period.ofYears(1)),
    LAST_5Y(Period.ofYears(5)),
    LAST_10Y(Period.ofYears(10)),
    LAST_3M(Period.ofMonths(3));

    private final Period duration;

    LookbackPeriod(Period duration) {
        this.duration = duration;
    }
}
