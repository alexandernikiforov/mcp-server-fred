package ch.alni.mcp.fred.adapter.http;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({FredApiProperties.class, FredRetryProperties.class})
class FredApiClientConfiguration {
}
