package ch.alni.mcp.fred.service.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FredServiceProperties.class)
public class FredServiceConfiguration {
}
