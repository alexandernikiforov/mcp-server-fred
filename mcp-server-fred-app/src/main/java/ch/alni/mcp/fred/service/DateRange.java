package ch.alni.mcp.fred.service;

import jakarta.validation.constraints.NotNull;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Represents a range of dates with a defined start and end (both inclusive). This class is useful for specifying a time
 * interval and is immutable.
 */
@ValidDateRange
public record DateRange(@NotNull LocalDate start, @NotNull LocalDate end) {

    public static DateRange of(LookbackPeriod lookbackPeriod, Clock clock) {
        return new DateRange(LocalDate.now(clock).minus(lookbackPeriod.getDuration()), LocalDate.now(clock));
    }
}
