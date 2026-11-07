package ch.alni.mcp.edgar.service.impl;

import ch.alni.mcp.edgar.service.Concept;
import ch.alni.mcp.edgar.service.FilingFactResponse;
import ch.alni.mcp.edgar.service.impl.XBRLInstanceParser.XBRLInstance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

final class FilingFactConverter {

    private static final Map<String, String> STANDARD_UNITS = Map.of(
            "http://www.xbrl.org/2003/iso4217", "iso4217",
            "http://www.xbrl.org/2003/instance", "xbrli"
    );

    private FilingFactConverter() {
    }

    static List<FilingFactResponse.FilingFact> toFilingFacts(XBRLInstance instance, Set<Concept> concepts) {
        // facts grouped together by (concept, unit, dimensions)
        final Map<FactKey, List<FactWithPeriod>> groupedFacts = instance.facts().stream()
                .filter(fact -> concepts.contains(Concept.of(fact.concept())))
                .collect(Collectors.groupingBy(fact -> {
                            final var context = instance.contexts().get(fact.contextRef());
                            return new FactKey(
                                    fact.concept(),
                                    toUnit(fact.unitRef(), instance.units()),
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
                        Collectors.mapping(
                                entry -> toObservation(entry.getKey(), entry.getValue()),
                                Collectors.toList())))
                .entrySet().stream()
                .map(entry -> new FilingFactResponse.FilingFact(
                        entry.getKey(),
                        entry.getValue()))
                .toList();
    }

    static FilingFactResponse.Observation toObservation(FactKey factKey, List<FactWithPeriod> facts) {
        return new FilingFactResponse.Observation(
                factKey.unitRef(),
                factKey.dimensions(),
                facts.stream()
                        .sorted(Comparator.comparing(FactWithPeriod::end))
                        .map(fact -> new FilingFactResponse.PeriodValue(
                                fact.start(),
                                fact.end(),
                                fact.value())
                        )
                        .distinct()
                        .collect(Collectors.toList()));
    }

    static String toUnit(String unitRef, Map<String, XBRLInstance.Unit> units) {
        final XBRLInstance.Unit unit = units.get(unitRef);
        if (unit == null) {
            // fallback although impossible
            return unitRef;
        } else {
            return format(unit);
        }
    }

    static String format(XBRLInstance.Unit unit) {
        final List<String> numerators = unit.numerator().stream()
                .map(qname -> STANDARD_UNITS.getOrDefault(qname.getNamespaceURI(), qname.toString()) + ":" + qname.getLocalPart())
                .toList();

        final List<String> denominators = unit.denominator().stream()
                .map(qname -> STANDARD_UNITS.getOrDefault(qname.getNamespaceURI(), qname.toString()) + ":" + qname.getLocalPart())
                .toList();

        final String top = String.join("*", numerators);

        if (denominators.isEmpty()) {
            return top;
        }

        final String bottom = String.join("*", denominators);

        return (numerators.size() > 1 ? "(" + top + ")" : top)
                + "/"
                + (denominators.size() > 1 ? "(" + bottom + ")" : bottom);
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
