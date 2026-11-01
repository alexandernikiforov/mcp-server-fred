package ch.alni.mcp.edgar.port;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record DirectoryResponse(Directory directory) {

    @Builder
    public record Directory(
            @JsonProperty("item")
            List<Item> items,
            String name,
            String parentDir
    ) {
    }

    @Builder
    public record Item(LocalDateTime lastModified, String name, String type, String size) {
    }
}
