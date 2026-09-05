package ch.alni.mcp.edgar.adapter.webclient;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({EdgarDataHttpClient.class, EdgarArchiveHttpClient.class})
class EdgarClientTestConfiguration {

}
