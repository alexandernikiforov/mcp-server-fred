package ch.alni.mcp.edgar.adapter.http;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@Getter
@ConfigurationProperties(prefix = "edgar.http")
public class EdgarHttpClientProperties {

    /**
     * The URL of the Edgar service.
     */
    private final String dataBaseUrl;

    /**
     * The URL of the Edgar service.
     */
    private final String archiveBaseUrl;

    /**
     * The maximum time to wait when attempting to establish a connection to the Edgar service.
     */
    private final Duration connectTimeout;

    /**
     * The maximum time to wait for data to be transferred over an established connection.
     */
    private final Duration readTimeout;

    /**
     * The User-Agent header to use for Edgar API calls. SEC requires this.
     */
    private final String userAgent;

    public EdgarHttpClientProperties(@DefaultValue("https://data.sec.gov") String dataBaseUrl,
                                     @DefaultValue("https://www.sec.gov/") String archiveBaseUrl,
                                     @DefaultValue("30s") Duration connectTimeout,
                                     @DefaultValue("30s") Duration readTimeout,
                                     @DefaultValue("mcp-server-edgar (your@email.com)") String userAgent) {
        this.dataBaseUrl = dataBaseUrl;
        this.archiveBaseUrl = archiveBaseUrl;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.userAgent = userAgent;
    }
}
