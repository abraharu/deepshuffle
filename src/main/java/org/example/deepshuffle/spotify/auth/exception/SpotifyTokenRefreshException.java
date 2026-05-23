package org.example.deepshuffle.spotify.auth.exception;

public class SpotifyTokenRefreshException extends RuntimeException {

    public SpotifyTokenRefreshException(String message) {
        super(message);
    }

    public SpotifyTokenRefreshException(String message, Throwable cause) {
        super(message, cause);
    }
}
