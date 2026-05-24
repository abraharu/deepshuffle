package org.example.deepshuffle.bot.taste;

import org.example.deepshuffle.spotify.taste.model.UserTasteSnapshot;
import org.springframework.stereotype.Component;

@Component
public class TasteMessageFormatter {

    public String loading() {
        return """
                Building taste fingerprint

                Status: reading your Spotify taste signals
                Sources: top artists, top tracks, liked tracks
                """;
    }

    public String success(UserTasteSnapshot snapshot) {
        return """
                Taste fingerprint updated

                Top artists saved: %d
                Top tracks saved: %d
                Liked tracks saved: %d
                Randomness level: %d/100

                Next: shuffle can use this profile for controlled randomness.
                """.formatted(
                snapshot.topArtistsCount(),
                snapshot.topTracksCount(),
                snapshot.likedTracksCount(),
                snapshot.randomnessLevel()
        );
    }

    public String authRequired(String loginUrl) {
        return """
                Connect Spotify first

                Taste fingerprint needs access to your top music and liked tracks.

                %s
                """.formatted(loginUrl);
    }

    public String failure() {
        return """
                Taste sync failed

                Status: Spotify taste data is unavailable right now
                Next step: try again in a moment
                """;
    }
}
