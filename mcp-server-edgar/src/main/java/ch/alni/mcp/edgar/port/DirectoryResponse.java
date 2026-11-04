package ch.alni.mcp.edgar.port;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Builder
public record DirectoryResponse(Directory directory) {

    @Builder
    public record Directory(
            @JsonProperty("item")
            List<Item> items,
            String name,
            String parentDir
    ) {
        /**
         * Finds the first filename in the directory that satisfies the given predicate.
         *
         * @param predicate a condition that each {@link DirectoryResponse.Item} will be tested against
         * @return an {@link Optional} containing the name of the first matching {@link DirectoryResponse.Item},
         * or an empty {@link Optional} if no matches are found
         */
        public String getItemName(Predicate<DirectoryResponse.Item> predicate) {
            return items().stream()
                    .filter(predicate)
                    .map(DirectoryResponse.Item::name)
                    .findFirst()
                    .orElseThrow();
        }

        /**
         * Checks if any {@link DirectoryResponse.Item} in the directory satisfies the given predicate.
         *
         * @param predicate a condition that each {@link DirectoryResponse.Item} will be tested against
         * @return true if any {@link DirectoryResponse.Item} matches the predicate, false otherwise
         */
        public boolean containsItem(Predicate<DirectoryResponse.Item> predicate) {
            return items().stream()
                    .anyMatch(predicate);
        }
    }

    @Builder
    public record Item(LocalDateTime lastModified, String name, String type, String size) {
    }

}
