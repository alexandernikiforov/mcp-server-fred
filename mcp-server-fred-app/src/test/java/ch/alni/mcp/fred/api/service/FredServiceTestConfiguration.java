package ch.alni.mcp.fred.api.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

@ComponentScan
@Configuration
class FredServiceTestConfiguration {

    @Bean
    @Primary
    WebClient fredWebClient(WebClient.Builder webClientBuilder) {
        final int port = MockWebServerProvider.WEB_SERVER.getPort();
        final String baseUrl = String.format("http://localhost:%s", port);
        return webClientBuilder.baseUrl(baseUrl).build();
    }

}
