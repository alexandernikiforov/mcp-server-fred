package ch.alni.mcp.edgar.adapter.http;

import ch.alni.mcp.edgar.port.EdgarArchiveClient;
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
    public Mono<String> getAccession(long cik, String accession, String primaryDocument) {
        return archiveWebClient.get()
                .uri(EdgarApiPaths.ACCESSION_PATH, CikFormatter.formatCik(cik), accession, primaryDocument)
                .retrieve()
                .bodyToMono(String.class);
    }
}
