package org.example.deepshuffle.spotify.playback;

public record SpotifyPlaybackResult(String playlistId,
                                    String playlistUri,
                                    String playlistUrl,
                                    SpotifyDevice device,
                                    String statusMessage) {
}
