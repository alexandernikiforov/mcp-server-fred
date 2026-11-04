package ch.alni.mcp.edgar.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record FilingFactResponse(List<FilingFact> facts) {

    public record FilingFact(
            String concept,
            List<Observation> observations
    ) {
    }

    @Builder
    public record Observation(String unitRef, PeriodValue reported, List<PeriodValue> comparatives) {
    }

    public record PeriodValue(
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @Nullable LocalDate start,
            LocalDate end,
            BigDecimal value
    ) {
    }

}
