package ch.alni.mcp.fred.client.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(FredClientProperties.class)
class FredClientConfiguration {

    private final FredClientProperties properties;

    FredClientConfiguration(FredClientProperties properties) {
        this.properties = properties;
    }

    @Bean
    WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl(properties.getBaseUrl())
                .build();
    }
}
