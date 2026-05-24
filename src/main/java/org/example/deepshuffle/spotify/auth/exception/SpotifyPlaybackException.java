package org.example.deepshuffle.spotify.auth.exception;

import org.example.deepshuffle.spotify.playback.SpotifyPlaybackError;

public class SpotifyPlaybackException extends RuntimeException {

    private final SpotifyPlaybackError error;

    public SpotifyPlaybackException(SpotifyPlaybackError error) {
        super(error.userMessage());
        this.error = error;
    }

    public SpotifyPlaybackException(SpotifyPlaybackError error, Throwable cause) {
        super(error.userMessage(), cause);
        this.error = error;
    }

    public SpotifyPlaybackException(String message, Throwable cause) {
        super(message, cause);
        this.error = SpotifyPlaybackError.UNAVAILABLE;
    }

    public SpotifyPlaybackError getError() {
        return error;
    }
}
