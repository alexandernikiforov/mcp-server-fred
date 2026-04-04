package ch.alni.mcp.edgar.adapter.webclient;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Getter
@ConfigurationProperties(prefix = "edgar.webclient.data")
public class EdgarDataWebClientProperties {

    /**
     * The URL of the Edgar data service.
     */
    private final String baseUrl;

    /**
     * Maximum size, in KiB, of an HTTP response that may be buffered in memory. Defaults to 16 MiB.
     */
    private final int maxHttpResponseMemorySizeInKib;

    public EdgarDataWebClientProperties(@DefaultValue("https://data.sec.gov/") String baseUrl,
                                        @DefaultValue("16384") int maxHttpResponseMemorySizeInKib) {
        this.baseUrl = baseUrl;
        this.maxHttpResponseMemorySizeInKib = maxHttpResponseMemorySizeInKib;
    }
}
