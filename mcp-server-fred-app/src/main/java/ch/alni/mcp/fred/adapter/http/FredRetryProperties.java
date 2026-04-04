package ch.alni.mcp.fred.adapter.http;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Getter
@ConfigurationProperties("fred.retry.too-many-requests")
public class FredRetryProperties {

    /**
     * How many times to retry in case of 429 status code (too many requests)
     */
    private final int maxAttempts;

    /**
     * The back-off interval for retry in case of 429 status code (too many requests)
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private final Duration backoff;

    public FredRetryProperties(@DefaultValue("2") int maxAttempts,
                               @DefaultValue("2s") Duration backoff) {
        this.maxAttempts = maxAttempts;
        this.backoff = backoff;
    }
}
