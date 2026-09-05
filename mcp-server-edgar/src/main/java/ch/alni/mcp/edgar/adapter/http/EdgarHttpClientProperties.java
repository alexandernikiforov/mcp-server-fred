package ch.alni.mcp.edgar.adapter.http;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

/**
 * Configuration properties for the Edgar HTTP client. These properties define timeouts and the User-Agent used for HTTP
 * requests when interacting with the Edgar service.
 *
 * @param connectTimeout The maximum time to wait when attempting to establish a connection to the Edgar service.
 *                       Defaults to 30 seconds.
 * @param readTimeout    The maximum time to wait for data to be transferred over an established connection. Defaults to
 *                       30 seconds.
 *
 */
@ConfigurationProperties(prefix = "edgar.http")
public record EdgarHttpClientProperties(
        @DefaultValue("30s") Duration connectTimeout,
        @DefaultValue("30s") Duration readTimeout
) {
}
