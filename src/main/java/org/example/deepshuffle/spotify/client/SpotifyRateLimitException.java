package org.example.deepshuffle.spotify.client;

public class SpotifyRateLimitException extends RuntimeException {

    private final int retryAfterSeconds;

    public SpotifyRateLimitException(String message, int retryAfterSeconds, Throwable cause) {
        super(message, cause);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public int getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
