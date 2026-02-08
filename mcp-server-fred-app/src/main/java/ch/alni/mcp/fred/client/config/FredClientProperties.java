package ch.alni.mcp.fred.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fred.client")
@Data
public class FredClientProperties {

    private String apiKey;
    private String baseUrl;
}
