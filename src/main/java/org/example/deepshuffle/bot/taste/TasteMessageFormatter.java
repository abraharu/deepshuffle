package org.example.deepshuffle.bot.taste;

import org.example.deepshuffle.spotify.taste.model.UserTasteSnapshot;
import org.springframework.stereotype.Component;

@Component
public class TasteMessageFormatter {

    public String loading() {
        return """
                🧬 Building your taste fingerprint

                Status: reading Spotify taste signals
                Sources: top artists, top tracks, liked tracks

                This may take a few seconds if Spotify asks us to slow down.
                """;
    }

    public String success(UserTasteSnapshot snapshot) {
        return """
                ✅ Taste fingerprint updated

                🎤 Top artists: %d
                🎵 Top tracks: %d
                💚 Liked tracks: %d
                🎚 Randomness: %d/100 — %s

                Next: choose how adventurous DeepShuffle should be.
                """.formatted(
                snapshot.topArtistsCount(),
                snapshot.topTracksCount(),
                snapshot.likedTracksCount(),
                snapshot.randomnessLevel(),
                randomnessLabel(snapshot.randomnessLevel())
        );
    }

    public String authRequired(String loginUrl) {
        return """
                🔐 Reconnect Spotify

                Taste fingerprint needs fresh access to your top music and liked tracks.

                %s
                """.formatted(loginUrl);
    }

    public String failure() {
        return """
                ⚠️ Taste sync failed

                Status: Spotify taste data is unavailable right now
                Next step: try again in a moment or reconnect Spotify
                """;
    }

    public String randomnessSettings(int currentLevel) {
        return """
                🎚 Discovery randomness

                Current level: %d/100 — %s

                Safe keeps close to your taste.
                Balanced explores nearby scenes.
                Deep goes underground.
                Chaos is pure DeepShuffle energy.
                """.formatted(currentLevel, randomnessLabel(currentLevel));
    }

    public String randomnessUpdated(int level) {
        return """
                ✅ Randomness updated

                New level: %d/100 — %s

                Future personalized shuffle can use this as the chaos dial.
                """.formatted(level, randomnessLabel(level));
    }

    private String randomnessLabel(int level) {
        if (level < 30) {
            return "Safe";
        }
        if (level < 65) {
            return "Balanced";
        }
        if (level < 90) {
            return "Deep";
        }
        return "Chaos";
    }
}
