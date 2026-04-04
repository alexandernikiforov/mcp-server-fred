package ch.alni.mcp.edgar.adapter.webclient;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Getter
@ConfigurationProperties(prefix = "edgar.webclient.archive")
public class EdgarArchiveWebClientProperties {

    /**
     * The URL of the Edgar service.
     */
    private final String baseUrl;

    public EdgarArchiveWebClientProperties(@DefaultValue("https://www.sec.gov/") String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
