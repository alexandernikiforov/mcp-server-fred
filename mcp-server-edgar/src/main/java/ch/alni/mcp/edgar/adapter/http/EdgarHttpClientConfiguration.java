package ch.alni.mcp.edgar.adapter.http;

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
@EnableConfigurationProperties({EdgarHttpDataClientProperties.class, EdgarHttpArchiveClientProperties.class})
class EdgarHttpClientConfiguration {

    private final EdgarHttpDataClientProperties dataClientProperties;
    private final EdgarHttpArchiveClientProperties archiveClientProperties;

    EdgarHttpClientConfiguration(EdgarHttpDataClientProperties dataClientProperties,
                                 EdgarHttpArchiveClientProperties archiveClientProperties) {
        this.dataClientProperties = dataClientProperties;
        this.archiveClientProperties = archiveClientProperties;
    }

    @Bean
    WebClient edgarDataWebClient(WebClient.Builder webClientBuilder) {
        final HttpClient httpClient = new ReactorHttpClientBuilder().build(HttpClientSettings.defaults()
                .withReadTimeout(dataClientProperties.getReadTimeout())
                .withConnectTimeout(dataClientProperties.getConnectTimeout()));
        final ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        return webClientBuilder.baseUrl(dataClientProperties.getBaseUrl())
                .defaultHeader("User-Agent", dataClientProperties.getUserAgent())
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(dataClientProperties.getMaxHttpResponseMemorySizeInKib() * 1024))
                .clientConnector(connector)
                .build();
    }

    @Bean
    WebClient edgarArchiveWebClient(WebClient.Builder webClientBuilder) {
        final HttpClient httpClient = new ReactorHttpClientBuilder().build(HttpClientSettings.defaults()
                .withReadTimeout(archiveClientProperties.getReadTimeout())
                .withConnectTimeout(archiveClientProperties.getConnectTimeout()));
        final ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        return webClientBuilder.baseUrl(archiveClientProperties.getBaseUrl())
                .defaultHeader("User-Agent", archiveClientProperties.getUserAgent())
                .clientConnector(connector)
                .build();
    }

}
