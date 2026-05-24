package org.example.deepshuffle.spotify.playback;

import java.util.Arrays;

public enum SpotifyPlaybackError {

    PREMIUM_REQUIRED(403, "Spotify Premium is required for remote playback"),
    DEVICE_NOT_FOUND(404, "Open Spotify on one of your devices first"),
    RATE_LIMITED(429, "Spotify rate limit reached. Try again in a minute"),
    UNAVAILABLE(-1, "Spotify playback is unavailable right now");

    private final int httpStatus;
    private final String userMessage;

    SpotifyPlaybackError(int httpStatus, String userMessage) {
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
    }

    public String userMessage() {
        return userMessage;
    }

    public static SpotifyPlaybackError fromStatus(int httpStatus) {
        return Arrays.stream(values())
                .filter(error -> error.httpStatus == httpStatus)
                .findFirst()
                .orElse(UNAVAILABLE);
    }
}
