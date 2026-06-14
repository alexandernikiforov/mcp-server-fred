package ch.alni.mcp.fred.service;

import lombok.Getter;

@Getter
public enum Language {
    RUSSIAN("Russian"),
    ENGLISH("English");

    private final String lang;

    Language(String lang) {
        this.lang = lang;
    }
}
