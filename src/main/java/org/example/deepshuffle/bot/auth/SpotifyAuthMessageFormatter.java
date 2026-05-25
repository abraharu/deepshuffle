package org.example.deepshuffle.bot.auth;

import org.springframework.stereotype.Component;

@Component
public class SpotifyAuthMessageFormatter {

    public String reconnectRequired() {
        return """
                🔐 Reconnect Spotify

                DeepShuffle needs fresh Spotify permissions to use taste fingerprint features:
                top artists, top tracks, and liked tracks.

                After reconnecting, return here and press Sync Taste again.
                """;
    }

    public String connectRequired() {
        return """
                🔐 Connect Spotify

                DeepShuffle needs Spotify access before it can start playback or build your taste fingerprint.
                """;
    }
}
