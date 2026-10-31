package ch.alni.mcp.edgar.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
public record ConceptFilingHistory(String taxonomy, String tag, String description, List<Filing> filings) {

    @Builder
    public record Filing(
            String accn,
            String form,
            String fp,
            int fy,
            LocalDate filed,
            List<Observation> observations
    ) {
    }

    @Builder
    public record Observation(String unit, PeriodValue reported, List<PeriodValue> comparatives) {
    }

    public record PeriodValue(
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @Nullable LocalDate start,
            LocalDate end,
            BigDecimal value
    ) {
    }
}
