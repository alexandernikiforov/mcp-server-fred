package ch.alni.mcp.edgar.adapter.webclient;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.unit.DataSize;

import java.net.URI;

/**
 * Configuration properties for the Edgar WebClient. These properties configure settings for interacting with the Edgar
 * services, including user agent information and configurations for data and archive endpoints.
 *
 * @param userAgent The User-Agent header to use for Edgar API calls. This parameter is required by the SEC for client
 *                  identification. Defaults to "mcp-server-edgar (your@email.com)".
 * @param data      The configuration properties specific to the Edgar data service.
 * @param archive   The configuration properties specific to the Edgar archive service.
 */
@ConfigurationProperties(prefix = "edgar.webclient")
public record EdgarWebClientProperties(
        @DefaultValue("mcp-server-edgar (your@email.com)") String userAgent,
        @DefaultValue Data data,
        @DefaultValue Archive archive
) {
    /**
     * Represents configuration properties for the Edgar archive WebClient.
     * This configuration is used to specify the base URL and the maximum size of
     * the in-memory buffer for HTTP responses when interacting with the Edgar archive service.
     *
     * @param baseUrl
     *        The base URL of the Edgar archive service. Defaults to "https://www.sec.gov".
     *
     * @param maxInMemorySize
     *        The maximum size of an HTTP response that may be buffered in memory. Defaults to 16 MiB.
     */
    public record Archive(
            @DefaultValue("https://www.sec.gov") URI baseUrl,
            @DefaultValue("16MB") DataSize maxInMemorySize
    ) {
    }

    /**
     * Represents configuration properties for the Edgar data WebClient.
     * This configuration specifies the base URL and the maximum in-memory buffer size
     * for HTTP responses when interacting with the Edgar data service.
     *
     * @param baseUrl
     *        The base URL of the Edgar data service. Defaults to "https://data.sec.gov".
     *
     * @param maxInMemorySize
     *        The maximum size of an HTTP response that may be buffered in memory. Defaults to 16 MiB.
     */
    public record Data(
            @DefaultValue("https://data.sec.gov") URI baseUrl,
            @DefaultValue("16MB") DataSize maxInMemorySize
    ) {
    }

}
