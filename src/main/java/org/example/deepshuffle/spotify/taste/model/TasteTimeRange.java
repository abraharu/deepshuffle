package org.example.deepshuffle.spotify.taste.model;

public enum TasteTimeRange {

    SHORT_TERM("short_term"),
    MEDIUM_TERM("medium_term"),
    LONG_TERM("long_term");

    private final String spotifyValue;

    TasteTimeRange(String spotifyValue) {
        this.spotifyValue = spotifyValue;
    }

    public String spotifyValue() {
        return spotifyValue;
    }
}
