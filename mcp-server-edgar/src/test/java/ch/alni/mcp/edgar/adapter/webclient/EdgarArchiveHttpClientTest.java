package ch.alni.mcp.edgar.adapter.webclient;

import ch.alni.mcp.edgar.port.DirectoryResponse;
import ch.alni.mcp.edgar.port.EdgarArchiveClient;
import mockwebserver3.MockResponse;
import mockwebserver3.RecordedRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class EdgarArchiveHttpClientTest extends EdgarClientTestSupport {

    private static final String ACCESSION = "000032019326000006";

    @Value("classpath:/responses/directory-response.json")
    private Resource directoryResponseResource;

    @Autowired
    private EdgarArchiveClient client;

    @Test
    void testDirectoryResponse() throws Exception {
        server.enqueue(new MockResponse.Builder()
                .body(directoryResponseResource.getContentAsString(StandardCharsets.UTF_8))
                .addHeader("Content-Type", "application/json")
                .build()
        );

        final int cik = 320193;
        final Mono<DirectoryResponse> responseMono = client.getDirectory(cik, ACCESSION);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> {
                    assertThat(response.directory().name()).contains("000032019326000006");
                    assertThat(response.directory().items())
                            .extracting(DirectoryResponse.Item::name)
                            .anySatisfy(name -> assertThat(name).endsWith("_pre.xml"));

                    return true;
                })
                .verifyComplete();

        // check request
        final RecordedRequest recordedRequest = server.takeRequest(10, TimeUnit.SECONDS);
        assertThat(recordedRequest).isNotNull();

        assertThat(recordedRequest.getUrl().host()).isEqualTo("localhost");
        assertThat(recordedRequest.getUrl().encodedPath()).isEqualTo(EdgarArchivePaths.ACCESSION_DOCUMENT_PATH
                .replace("{cik}", Integer.toString(cik))
                .replace("{accession}", ACCESSION)
                .replace("{primaryDocument}", "index.json")
        );
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
    }
}
