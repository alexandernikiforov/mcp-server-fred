package ch.alni.mcp.edgar.adapter.webclient;

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
@EnableConfigurationProperties({
        EdgarWebClientProperties.class,
        EdgarDataWebClientProperties.class,
        EdgarArchiveWebClientProperties.class
})
class EdgarWebClientConfiguration {

    private final EdgarWebClientProperties properties;
    private final EdgarDataWebClientProperties dataClientProperties;
    private final EdgarArchiveWebClientProperties archiveClientProperties;

    EdgarWebClientConfiguration(EdgarWebClientProperties properties,
                                EdgarDataWebClientProperties dataClientProperties,
                                EdgarArchiveWebClientProperties archiveClientProperties) {
        this.properties = properties;
        this.dataClientProperties = dataClientProperties;
        this.archiveClientProperties = archiveClientProperties;
    }

    @Bean
    WebClient edgarDataWebClient(WebClient.Builder webClientBuilder) {
        final HttpClient httpClient = new ReactorHttpClientBuilder().build(HttpClientSettings.defaults()
                .withReadTimeout(properties.getReadTimeout())
                .withConnectTimeout(properties.getConnectTimeout()));
        final ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        return webClientBuilder.baseUrl(dataClientProperties.getBaseUrl())
                .defaultHeader("User-Agent", properties.getUserAgent())
                .defaultHeader("Accept-Encoding", "gzip", "deflate")
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(dataClientProperties.getMaxHttpResponseMemorySizeInKib() * 1024))
                .clientConnector(connector)
                .build();
    }

    @Bean
    WebClient edgarArchiveWebClient(WebClient.Builder webClientBuilder) {
        final HttpClient httpClient = new ReactorHttpClientBuilder().build(HttpClientSettings.defaults()
                .withReadTimeout(properties.getReadTimeout())
                .withConnectTimeout(properties.getConnectTimeout()));
        final ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        return webClientBuilder.baseUrl(archiveClientProperties.getBaseUrl())
                .defaultHeader("User-Agent", properties.getUserAgent())
                .defaultHeader("Accept-Encoding", "gzip", "deflate")
                .clientConnector(connector)
                .build();
    }

}
