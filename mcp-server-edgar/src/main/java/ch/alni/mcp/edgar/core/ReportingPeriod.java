package ch.alni.mcp.edgar.core;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * TODO: javadoc
 */
public enum ReportingPeriod {
    UNSPECIFIED,
    ONE_QUARTER,
    TWO_QUARTERS,
    THREE_QUARTERS,
    ONE_YEAR;

    public static boolean isSamePeriod(@Nullable LocalDate start,
                                       @NonNull LocalDate end,
                                       @Nullable LocalDate otherStart,
                                       @NonNull LocalDate otherEnd) {

        final ReportingPeriod reportingPeriod = classifyPeriod(start, end);
        final ReportingPeriod otherReportingPeriod = classifyPeriod(otherStart, otherEnd);
        return reportingPeriod == otherReportingPeriod;
    }

    public static ReportingPeriod classifyPeriod(@Nullable LocalDate start, @NonNull LocalDate end) {
        if (start == null) {
            return UNSPECIFIED;
        }

        long days = ChronoUnit.DAYS.between(start, end) + 1;

        if (days >= 80 && days <= 100) {
            return ONE_QUARTER;
        }
        if (days >= 170 && days <= 195) {
            return TWO_QUARTERS;
        }
        if (days >= 260 && days <= 285) {
            return THREE_QUARTERS;
        }
        if (days >= 350 && days <= 380) {
            return ONE_YEAR;
        }

        return UNSPECIFIED;
    }
}
