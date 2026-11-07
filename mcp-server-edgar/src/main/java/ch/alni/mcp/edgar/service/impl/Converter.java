package ch.alni.mcp.edgar.service.impl;


import ch.alni.mcp.edgar.core.ReportingPeriod;
import ch.alni.mcp.edgar.port.CompanyFact;
import ch.alni.mcp.edgar.port.CompanyFactsResponse;
import ch.alni.mcp.edgar.port.CompanySubmissionsResponse;
import ch.alni.mcp.edgar.service.Concept;
import ch.alni.mcp.edgar.service.ConceptFilingHistory;
import ch.alni.mcp.edgar.service.FilingHistory.Filing;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ch.alni.mcp.edgar.service.ConceptFilingHistory.Observation;
import static ch.alni.mcp.edgar.service.ConceptFilingHistory.PeriodValue;

final class Converter {
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

    static List<ConceptFilingHistory.Filing> toFilings(CompanyFactsResponse.FactDefinition factDefinition, int fiscalYearCount) {
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

    static ConceptFilingHistory.Filing toFiling(FactKey factKey, List<Observation> observations) {
        return new ConceptFilingHistory.Filing(
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

        final Map<ReportingPeriod, List<CompanyFact>> factsByPeriod =
                companyFacts.stream()
                        .collect(Collectors.groupingBy(
                                fact -> ReportingPeriod.classifyPeriod(fact.start(), fact.end())
                        ));

        return factsByPeriod.entrySet().stream()
                .map(entry -> new Observation(
                        unit,
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(fact -> new PeriodValue(
                                        fact.start(),
                                        fact.end(),
                                        fact.val()
                                ))
                                .sorted(Comparator.comparing(PeriodValue::end)
                                        .thenComparing(
                                                PeriodValue::start,
                                                Comparator.nullsFirst(Comparator.naturalOrder()))
                                )
                                .toList()
                ))
                .toList();

    }

    static List<Filing> toFilings(CompanySubmissionsResponse response, int latestSubmissions) {
        final CompanySubmissionsResponse.RecentFilings filings = response.filings().recent();
        return IntStream.range(0, filings.form().size())
                .filter(i -> SUPPORTED_FORMS.contains(filings.form().get(i)))
                .limit(latestSubmissions)
                .mapToObj(i -> new Filing(
                        filings.accessionNumber().get(i),
                        filings.filingDate().get(i),
                        LocalDate.parse(filings.reportDate().get(i)),
                        filings.form().get(i),
                        filings.primaryDocument().get(i)
                ))
                .toList();
    }

    record FactKey(String accn, int fy, String fp, String form, LocalDate filed) {
    }
}
