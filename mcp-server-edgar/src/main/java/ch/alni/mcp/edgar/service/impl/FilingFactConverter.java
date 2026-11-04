package ch.alni.mcp.edgar.service.impl;

import ch.alni.mcp.edgar.service.FilingFactResponse;
import ch.alni.mcp.edgar.service.impl.XBRLInstanceParser.XBRLInstance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.alni.mcp.edgar.service.impl.Converter.isSamePeriod;

final class FilingFactConverter {
    private FilingFactConverter() {
    }

    static List<FilingFactResponse.FilingFact> toFilingFacts(XBRLInstance instance) {
        final Map<FactKey, List<FactWithPeriod>> groupedFacts = instance.facts().stream()
                .collect(Collectors.groupingBy(fact -> {
                            final var context = instance.contexts().get(fact.contextRef());
                            return new FactKey(
                                    fact.concept(),
                                    fact.unitRef(),
                                    context.dimensions());
                        },
                        Collectors.mapping(fact -> {
                            final var context = instance.contexts().get(fact.contextRef());
                            return new FactWithPeriod(
                                    fact.concept(),
                                    fact.value(),
                                    context.startDate(),
                                    context.instant() != null ? context.instant() : context.endDate()
                            );
                        }, Collectors.toList())));

        return groupedFacts.entrySet().stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getKey().concept(),
                        Collectors.flatMapping(
                                entry -> toObservations(entry.getKey(), entry.getValue()).stream(),
                                Collectors.toList())))
                .entrySet().stream()
                .map(entry -> new FilingFactResponse.FilingFact(
                        entry.getKey(),
                        entry.getValue()))
                .toList();
    }

    static List<FilingFactResponse.Observation> toObservations(FactKey factKey, List<FactWithPeriod> facts) {
        if (facts.isEmpty()) {
            return List.of();
        }

        // find the maximum end value
        final Optional<LocalDate> maxEnd = facts.stream()
                .map(FactWithPeriod::end)
                .max(Comparator.naturalOrder());

        final List<FactWithPeriod> latestFacts = maxEnd
                .map(end -> facts.stream()
                        .filter(fact -> end.equals(fact.end()))
                        .toList())
                .orElseGet(List::of);

        final List<FactWithPeriod> comparatives = maxEnd
                .map(end -> facts.stream()
                        .filter(fact -> !end.equals(fact.end()))
                        .toList())
                .orElseGet(List::of);

        return latestFacts.stream()
                .map(latestFact -> new FilingFactResponse.Observation(
                        factKey.unitRef(),
                        factKey.dimensions(),
                        new FilingFactResponse.PeriodValue(latestFact.start(), latestFact.end(), latestFact.value()),
                        selectComparatives(latestFact, comparatives).stream()
                                .map(comparative -> new FilingFactResponse.PeriodValue(
                                        comparative.start(),
                                        comparative.end(),
                                        comparative.value())
                                )
                                .toList()))
                .toList();
    }

    static List<FactWithPeriod> selectComparatives(FactWithPeriod reported, List<FactWithPeriod> facts) {
        return facts.stream()
                .filter(fact -> isSamePeriod(reported.start(), reported.end(), fact.start(), fact.end()))
                .toList();
    }

    private record FactKey(String concept, String unitRef, Map<String, String> dimensions) {
        private FactKey {
            dimensions = Map.copyOf(dimensions);
        }
    }

    private record FactWithPeriod(
            String concept,
            BigDecimal value,
            LocalDate start,
            LocalDate end
    ) {
    }
}
