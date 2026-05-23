package org.example.deepshuffle.spotify.auth.exception;

public class SpotifyPlaybackException extends RuntimeException {

    public SpotifyPlaybackException(String message) {
        super(message);
    }

    public SpotifyPlaybackException(String message, Throwable cause) {
        super(message, cause);
    }
}
