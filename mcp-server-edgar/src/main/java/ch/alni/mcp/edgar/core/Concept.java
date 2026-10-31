package ch.alni.mcp.edgar.core;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Concept(String taxonomy, String tag) {
    private static final Pattern CONCEPT_PATTERN = Pattern.compile("^(.+):(.+)$");

    public static Concept of(String value) {
        final Matcher matcher = CONCEPT_PATTERN.matcher(value);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid tag format: " + value);
        }
        return new Concept(matcher.group(1), matcher.group(2));
    }
}
