package ch.alni.mcp.fred.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

/**
 * JSON response to the observations request.
 *
 * @param realtimeStart
 * @param realtimeEnd
 * @param observationStart
 * @param observationEnd
 * @param units
 * @param outputType
 * @param orderBy
 * @param sortOrder
 * @param count
 * @param offset
 * @param limit
 * @param observations
 */
@Builder(toBuilder = true)
public record ObservationsResponse(
        @JsonProperty("realtime_start")
        LocalDate realtimeStart,
        @JsonProperty("realtime_end")
        LocalDate realtimeEnd,
        @JsonProperty("observation_start")
        LocalDate observationStart,
        @JsonProperty("observation_end")
        LocalDate observationEnd,
        String units,
        @JsonProperty("output_type")
        String outputType,
        @JsonProperty("order_by")
        String orderBy,
        @JsonProperty("sort_order")
        String sortOrder,
        int count,
        int offset,
        int limit,
        List<Observation> observations
) {
}
