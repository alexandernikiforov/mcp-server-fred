package ch.alni.mcp.fred.service;

import lombok.Getter;

import java.time.Period;

@Getter
public enum LookbackPeriod {
    LAST_1D(Period.ofDays(1)),
    LAST_1Y(Period.ofYears(1)),
    LAST_3Y(Period.ofYears(3)),
    LAST_1M(Period.ofMonths(1)),
    LAST_3M(Period.ofMonths(3));

    private final Period duration;

    LookbackPeriod(Period duration) {
        this.duration = duration;
    }
}
