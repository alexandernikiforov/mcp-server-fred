package ch.alni.mcp.fred.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder(toBuilder = true)
public record SeriesResponse(
        Series series,
        @JsonProperty("start_date")
        LocalDate startDate,
        @JsonProperty("end_date")
        LocalDate endDate,
        List<DataPoint> points
) {
}
