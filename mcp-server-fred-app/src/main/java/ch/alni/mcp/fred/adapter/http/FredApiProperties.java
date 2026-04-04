package ch.alni.mcp.fred.adapter.http;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "fred.api")
public class FredApiProperties {

    /**
     * The API key to use for calls to Fred API.
     */
    private final String key;

    public FredApiProperties(String key) {
        this.key = key;
    }

}
