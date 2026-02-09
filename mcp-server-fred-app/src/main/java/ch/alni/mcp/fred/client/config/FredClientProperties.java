package ch.alni.mcp.fred.client.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Getter
@ConfigurationProperties(prefix = "fred.client")
public class FredClientProperties {

    /**
     * The API key to use for calls to Fred API.
     */
    private final String apiKey;

    /**
     * The URL of the Fred service.
     */
    private final String baseUrl;

    /**
     * How many times to retry in case of 429 status code (too many requests)
     */
    private final int retries;

    /**
     * The back-off interval for retry in case of 429 status code (too many requests)
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private final Duration backoff;

    public FredClientProperties(String apiKey,
                                String baseUrl,
                                @DefaultValue("2")
                                int retries,
                                @DefaultValue("2s")
                                Duration backoff) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.retries = retries;
        this.backoff = backoff;
    }

}
