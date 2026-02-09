package ch.alni.mcp.fred.server;

import lombok.Builder;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record DataPoint(
        LocalDate date,
        String value
) {
}
