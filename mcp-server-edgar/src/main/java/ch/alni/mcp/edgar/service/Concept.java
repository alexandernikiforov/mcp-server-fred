package ch.alni.mcp.edgar.service;


import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Concept(
        @Schema(description = "XBRL taxonomy namespace (e.g., us-gaap)")
        String taxonomy,

        @Schema(description = "XBRL concept name within the taxonomy (e.g., Revenues)")
        String tag
) {
    private static final Pattern CONCEPT_PATTERN = Pattern.compile("^([^:]+):([^:]+)$");

    public Concept {
        if (taxonomy == null || taxonomy.isEmpty()) {
            throw new IllegalArgumentException("Taxonomy cannot be null or empty");
        }
        if (tag == null || tag.isEmpty()) {
            throw new IllegalArgumentException("Tag cannot be null or empty");
        }

        taxonomy = taxonomy.toLowerCase(Locale.ROOT).replace("_", "-");
    }

    public static Concept of(String value) {
        final Matcher matcher = CONCEPT_PATTERN.matcher(value);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid tag format: " + value);
        }
        return new Concept(matcher.group(1), matcher.group(2));
    }
}
