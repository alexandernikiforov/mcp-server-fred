package ch.alni.mcp.edgar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@SpringBootApplication
public class EdgarMcpServerApplication {

    static void main(String[] args) {
        SpringApplication.run(EdgarMcpServerApplication.class, args);
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

}
