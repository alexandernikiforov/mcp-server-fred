package ch.alni.mcp.fred.client;

import org.springframework.ai.mcp.server.common.autoconfigure.McpServerAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

@ComponentScan
@Configuration
@EnableAutoConfiguration(exclude = {McpServerAutoConfiguration.class})
class FredClientServiceTestConfiguration {

    @Bean
    @Primary
    WebClient webClient(WebClient.Builder webClientBuilder) {
        final int port = MockWebServerProvider.WEB_SERVER.getPort();
        final String baseUrl = String.format("http://localhost:%s", port);
        return webClientBuilder.baseUrl(baseUrl).build();
    }

}
