package ch.alni.mcp.edgar.adapter.webclient;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webclient.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(EdgarWebClientProperties.class)
class EdgarWebClientConfiguration {

    private final EdgarWebClientProperties webClientProperties;

    EdgarWebClientConfiguration(EdgarWebClientProperties webClientProperties) {
        this.webClientProperties = webClientProperties;
    }

    @Bean
    WebClientCustomizer webClientCustomizer() {
        return builder -> builder
                .defaultHeader("User-Agent", webClientProperties.userAgent())
                .defaultHeader("Accept-Encoding", "gzip", "deflate");
    }

    @Bean
    WebClient edgarDataWebClient(WebClient.Builder webClientBuilder) {
        final EdgarWebClientProperties.Data data = webClientProperties.data();
        return webClientBuilder.baseUrl(data.baseUrl().toString())
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize((int) data.maxInMemorySize().toBytes()))
                .build();
    }

    @Bean
    WebClient edgarArchiveWebClient(WebClient.Builder webClientBuilder) {
        final EdgarWebClientProperties.Archive archive = webClientProperties.archive();
        return webClientBuilder.baseUrl(archive.baseUrl().toString())
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize((int) archive.maxInMemorySize().toBytes()))
                .build();
    }

}
