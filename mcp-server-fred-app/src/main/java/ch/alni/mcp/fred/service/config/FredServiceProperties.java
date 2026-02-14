package ch.alni.mcp.fred.service.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Getter
@ConfigurationProperties(prefix = "fred.service")
public class FredServiceProperties {

    /**
     * The API key to use for calls to Fred API.
     */
    private final String apiKey;

    /**
     * How many times to retry in case of 429 status code (too many requests)
     */
    private final int retries;

    /**
     * The back-off interval for retry in case of 429 status code (too many requests)
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private final Duration backoff;

    public FredServiceProperties(String apiKey,
                                 @DefaultValue("2")
                                 int retries,
                                 @DefaultValue("2s")
                                 Duration backoff) {
        this.apiKey = apiKey;
        this.retries = retries;
        this.backoff = backoff;
    }

}
