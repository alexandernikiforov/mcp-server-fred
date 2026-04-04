package ch.alni.mcp.fred.adapter.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

@ComponentScan
@Configuration
class FredApiClientTestConfiguration {

    @Value("${fred.mock.port}")
    private int port;

    @Bean
    @Primary
    WebClient fredWebClient(WebClient.Builder webClientBuilder) {
        final String baseUrl = String.format("http://localhost:%s", port);
        return webClientBuilder.baseUrl(baseUrl).build();
    }

}
