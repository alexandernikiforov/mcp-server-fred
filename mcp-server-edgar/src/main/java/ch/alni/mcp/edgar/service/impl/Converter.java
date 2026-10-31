package ch.alni.mcp.edgar.service.impl;


import ch.alni.mcp.edgar.core.Concept;
import ch.alni.mcp.edgar.port.CompanyFact;
import ch.alni.mcp.edgar.port.CompanyFactsResponse;
import ch.alni.mcp.edgar.service.ConceptFilingHistory;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.alni.mcp.edgar.service.ConceptFilingHistory.Filing;
import static ch.alni.mcp.edgar.service.ConceptFilingHistory.Observation;
import static ch.alni.mcp.edgar.service.ConceptFilingHistory.PeriodValue;

final class Converter {
    public static final int MAX_LATEST_FILINGS = 12;
    private static final Set<String> SUPPORTED_FORMS = Set.of(
            "20-F",
            "20-F/A",
            "10-K",
            "10-K/A",
            "10-Q",
            "10-Q/A"
    );

    private Converter() {
    }

    static Optional<ConceptFilingHistory> toReportedHistory(CompanyFactsResponse factsResponse, Concept concept, int fiscalYearCount) {
        final CompanyFactsResponse.FactDefinition factDefinition = factsResponse.facts()
                .getOrDefault(concept.taxonomy(), Collections.emptyMap())
                .get(concept.tag());

        if (factDefinition == null) {
            return Optional.empty();
        }

        return Optional.of(new ConceptFilingHistory(
                concept.taxonomy(),
                concept.tag(),
                factDefinition.description(),
                toFilings(factDefinition, fiscalYearCount)
        ));
    }

    static List<Filing> toFilings(CompanyFactsResponse.FactDefinition factDefinition, int fiscalYearCount) {
        // list of observations grouped by fact key under the same unit
        final List<Map<FactKey, List<Observation>>> observations =
                factDefinition.units().entrySet().stream()
                        .map(entry -> Map.entry(
                                // unit
                                entry.getKey(),
                                // facts grouped by the fact key (accn, fy, fp, form, filed)
                                entry.getValue().stream()
                                        .filter(fact -> SUPPORTED_FORMS.contains(fact.form()))
                                        .collect(Collectors.groupingBy(fact -> new FactKey(
                                                fact.accn(),
                                                fact.fy(),
                                                fact.fp(),
                                                fact.form(),
                                                fact.filed()
                                        )))
                        ))
                        .map(entry -> entry.getValue().entrySet().stream()
                                .collect(Collectors.toMap(
                                        // fact key
                                        Map.Entry::getKey,
                                        // entry (fact key, company facts with the same key)
                                        factGroup -> toObservations(entry.getKey(), factGroup.getValue())
                                )))
                        .toList();

        // merge all observations under the same fact key
        final Map<FactKey, List<Observation>> mergedObservations = observations.stream()
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new ArrayList<>(entry.getValue()),
                        (left, right) -> {
                            left.addAll(right);
                            return left;
                        }
                ));

        final int latestFy = mergedObservations.keySet().stream()
                .mapToInt(FactKey::fy)
                .max()
                .orElse(0);

        return mergedObservations.entrySet().stream()
                .filter(entry -> entry.getKey().fy() > latestFy - fiscalYearCount)
                .sorted(Comparator.comparing(
                        (Map.Entry<FactKey, List<Observation>> entry) -> entry.getKey().filed()
                ).reversed())
                .map(entry -> toFiling(entry.getKey(), entry.getValue()))
                .toList();
    }

    static Filing toFiling(FactKey factKey, List<Observation> observations) {
        return new Filing(
                factKey.accn(),
                factKey.form(),
                factKey.fp(),
                factKey.fy(),
                factKey.filed(),
                observations);
    }

    static List<Observation> toObservations(String unit, List<CompanyFact> companyFacts) {
        if (companyFacts.isEmpty()) {
            return Collections.emptyList();
        }

        // find the maximum end value
        final Optional<LocalDate> maxEnd = companyFacts.stream()
                .map(CompanyFact::end)
                .max(Comparator.naturalOrder());

        final List<CompanyFact> latestFacts = maxEnd
                .map(end -> companyFacts.stream()
                        .filter(fact -> end.equals(fact.end()))
                        .toList())
                .orElseGet(List::of);

        final List<CompanyFact> comparatives = maxEnd
                .map(end -> companyFacts.stream()
                        .filter(fact -> !end.equals(fact.end()))
                        .toList())
                .orElseGet(List::of);

        return latestFacts.stream()
                .map(latestFact -> new Observation(
                        unit,
                        new PeriodValue(latestFact.start(), latestFact.end(), latestFact.val()),
                        selectComparatives(latestFact, comparatives).stream()
                                .map(comparative -> new PeriodValue(
                                        comparative.start(),
                                        comparative.end(),
                                        comparative.val())
                                )
                                .toList()))
                .toList();
    }

    static List<CompanyFact> selectComparatives(CompanyFact reported, List<CompanyFact> companyFacts) {
        return companyFacts.stream()
                .filter(fact -> isSamePeriod(reported.start(), reported.end(), fact.start(), fact.end()))
                .toList();
    }

    static boolean isSamePeriod(@Nullable LocalDate start,
                                @NonNull LocalDate end,
                                @Nullable LocalDate otherStart,
                                @NonNull LocalDate otherEnd) {

        final ReportingPeriod reportingPeriod = classifyPeriod(start, end);
        final ReportingPeriod otherReportingPeriod = classifyPeriod(otherStart, otherEnd);
        return reportingPeriod == otherReportingPeriod;
    }

    static ReportingPeriod classifyPeriod(@Nullable LocalDate start, @NonNull LocalDate end) {
        if (start == null) {
            return ReportingPeriod.UNSPECIFIED;
        }

        long days = ChronoUnit.DAYS.between(start, end) + 1;

        if (days >= 80 && days <= 100) {
            return ReportingPeriod.ONE_QUARTER;
        }
        if (days >= 170 && days <= 195) {
            return ReportingPeriod.TWO_QUARTERS;
        }
        if (days >= 260 && days <= 285) {
            return ReportingPeriod.THREE_QUARTERS;
        }
        if (days >= 350 && days <= 380) {
            return ReportingPeriod.ONE_YEAR;
        }

        return ReportingPeriod.UNSPECIFIED;
    }

    enum ReportingPeriod {
        UNSPECIFIED,
        ONE_QUARTER,
        TWO_QUARTERS,
        THREE_QUARTERS,
        ONE_YEAR
    }

    record FactKey(String accn, int fy, String fp, String form, LocalDate filed) {
    }
}
