package ch.alni.mcp.fred.adapter.webclient;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.boot.http.client.ReactorHttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableConfigurationProperties({FredWebClientProperties.class})
class FredWebClientConfiguration {

    private final FredWebClientProperties properties;

    FredWebClientConfiguration(FredWebClientProperties properties) {
        this.properties = properties;
    }

    @Bean
    WebClient fredWebClient(WebClient.Builder webClientBuilder) {
        final HttpClient httpClient = new ReactorHttpClientBuilder().build(HttpClientSettings.defaults()
                .withReadTimeout(properties.getReadTimeout())
                .withConnectTimeout(properties.getConnectTimeout()));
        final ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        return webClientBuilder.baseUrl(properties.getBaseUrl())
                .clientConnector(connector)
                .build();
    }

}
