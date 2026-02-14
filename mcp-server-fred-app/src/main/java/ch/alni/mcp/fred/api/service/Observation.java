package ch.alni.mcp.fred.api.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDate;

/**
 * A single observation.
 *
 * @param realtimeStart
 * @param realtimeEnd
 * @param date
 * @param value
 */
@Builder(toBuilder = true)
public record Observation(
        @JsonProperty("realtime_start")
        LocalDate realtimeStart,
        @JsonProperty("realtime_end")
        LocalDate realtimeEnd,
        LocalDate date,
        String value
) {
}
