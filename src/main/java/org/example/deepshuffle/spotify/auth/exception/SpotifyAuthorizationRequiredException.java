package org.example.deepshuffle.spotify.auth.exception;

public class SpotifyAuthorizationRequiredException extends RuntimeException {

    public SpotifyAuthorizationRequiredException(String message) {
        super(message);
    }
}
