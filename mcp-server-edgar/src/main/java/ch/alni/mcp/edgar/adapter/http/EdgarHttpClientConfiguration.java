package ch.alni.mcp.edgar.adapter.http;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.boot.http.client.ReactorHttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(EdgarHttpClientProperties.class)
public class EdgarHttpClientConfiguration {

    private final EdgarHttpClientProperties httpClientProperties;

    public EdgarHttpClientConfiguration(EdgarHttpClientProperties httpClientProperties) {
        this.httpClientProperties = httpClientProperties;
    }

    @Bean
    ClientHttpConnector edgarClientHttpConnector() {
        final HttpClientSettings settings = HttpClientSettings.defaults()
                .withReadTimeout(httpClientProperties.readTimeout())
                .withConnectTimeout(httpClientProperties.connectTimeout());

        final HttpClient httpClient = new ReactorHttpClientBuilder().build(settings);
        return new ReactorClientHttpConnector(httpClient);
    }
}
