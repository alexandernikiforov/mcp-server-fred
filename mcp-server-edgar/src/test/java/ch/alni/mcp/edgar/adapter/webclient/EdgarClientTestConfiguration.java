package ch.alni.mcp.edgar.adapter.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ComponentScan
class EdgarClientTestConfiguration {

    public static final int MAX_HTTP_RESPONSE_MEMORY_SIZE = 16 * 1024 * 1024;

    @Value("${edgar.mock.port}")
    private int port;

    @Bean
    @Primary
    WebClient edgarDataWebClient(WebClient.Builder webClientBuilder) {
        final String baseUrl = String.format("http://localhost:%s", port);
        return webClientBuilder.baseUrl(baseUrl)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(MAX_HTTP_RESPONSE_MEMORY_SIZE))
                .build();
    }

}
