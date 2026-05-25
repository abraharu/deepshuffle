package org.example.deepshuffle.bot.playback;

import org.example.deepshuffle.spotify.auth.exception.SpotifyPlaybackException;
import org.example.deepshuffle.spotify.playback.SpotifyDevice;
import org.example.deepshuffle.spotify.playback.SpotifyPlaybackError;
import org.example.deepshuffle.spotify.playback.SpotifyPlaybackResult;
import org.example.deepshuffle.spotify.dto.Playlist;
import org.springframework.stereotype.Component;

@Component
public class PlaybackMessageFormatter {

    public String loading(Playlist playlist) {
        return """
                ▶️ Starting playback

                🎧 Playlist: %s
                👤 Curator: %s
                Status: contacting Spotify
                """.formatted(playlist.name(), playlist.owner());
    }

    public String success(SpotifyPlaybackResult result, Playlist playlist) {
        return """
                ✅ Playback started

                🎧 Playlist: %s
                👤 Curator: %s
                🔊 Device: %s
                Status: %s

                %s
                """.formatted(
                playlist.name(),
                playlist.owner(),
                deviceLabel(result.device()),
                result.statusMessage(),
                playlist.url()
        );
    }

    public String authRequired(String loginUrl) {
        return """
                🔐 Connect Spotify first

                To start playback from Telegram, link your Spotify account.

                %s
                """.formatted(loginUrl);
    }

    public String failure(Playlist playlist, SpotifyPlaybackException exception) {
        SpotifyPlaybackError error = exception.getError();
        return """
                ⚠️ Playback needs attention

                🎧 Playlist: %s
                👤 Curator: %s
                Status: %s
                Next step: %s
                """.formatted(
                playlist.name(),
                playlist.owner(),
                error.userMessage(),
                recoveryHint(error)
        );
    }

    public String unexpectedFailure(Playlist playlist) {
        return """
                ⚠️ Playback failed

                🎧 Playlist: %s
                👤 Curator: %s
                Status: Spotify playback is unavailable right now
                Next step: try again in a moment
                """.formatted(playlist.name(), playlist.owner());
    }

    private String deviceLabel(SpotifyDevice device) {
        if (device == null) {
            return "unknown device";
        }

        String name = device.name() == null || device.name().isBlank() ? "Unnamed device" : device.name();
        String type = device.type() == null || device.type().isBlank() ? "Spotify device" : device.type();
        return "%s (%s)".formatted(name, type);
    }

    private String recoveryHint(SpotifyPlaybackError error) {
        return switch (error) {
            case DEVICE_NOT_FOUND -> "open Spotify on desktop or phone, then press play again";
            case PREMIUM_REQUIRED -> "remote playback requires Spotify Premium";
            case RATE_LIMITED -> "wait a minute before retrying";
            case UNAVAILABLE -> "try again in a moment";
        };
    }
}
