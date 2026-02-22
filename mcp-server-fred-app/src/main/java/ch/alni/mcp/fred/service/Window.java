package ch.alni.mcp.fred.service;

import java.time.Period;
import java.time.temporal.TemporalAmount;

public enum Window {
    LAST_1Y(Period.ofYears(1)),
    LAST_5Y(Period.ofYears(5)),
    LAST_10Y(Period.ofYears(10)),
    LAST_3M(Period.ofMonths(3));

    private final TemporalAmount duration;

    Window(TemporalAmount duration) {
        this.duration = duration;
    }

    public TemporalAmount getDuration() {
        return duration;
    }
}
