package ch.alni.mcp.edgar.adapter.webclient;

import ch.alni.mcp.edgar.port.EdgarArchiveClient;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
class EdgarArchiveHttpClient implements EdgarArchiveClient {

    private final WebClient archiveWebClient;

    EdgarArchiveHttpClient(WebClient edgarArchiveWebClient) {
        this.archiveWebClient = edgarArchiveWebClient;
    }

    @Override
    @RateLimiter(name = "edgar")
    public Mono<String> getAccession(long cik, String accession, String primaryDocument) {
        return archiveWebClient.get()
                .uri(EdgarArchivePaths.ACCESSION_PATH, cik, accession, primaryDocument)
                .retrieve()
                .bodyToMono(String.class);
    }
}
