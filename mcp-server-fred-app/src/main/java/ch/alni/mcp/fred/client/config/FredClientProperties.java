package ch.alni.mcp.fred.client.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@Getter
@ConfigurationProperties(prefix = "fred.client")
public class FredClientProperties {


    /**
     * The URL of the Fred service.
     */
    private final String baseUrl;

    /**
     * The maximum time to wait when attempting to establish a connection to the Fred service.
     * This value is defined as a {@link Duration} and allows the client to specify how long
     * it should wait before timing out a connection attempt.
     */
    private final Duration connectTimeout;

    /**
     * The maximum time to wait for data to be transferred over an established connection.
     */
    private final Duration readTimeout;

    public FredClientProperties(String baseUrl,
                                @DefaultValue("30s")
                                Duration connectTimeout,
                                @DefaultValue("30s")
                                Duration readTimeout) {
        this.baseUrl = baseUrl;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

}
