package org.example.deepshuffle.service;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.spotify.SpotifyClient;
import org.example.deepshuffle.spotify.dto.Playlist;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ShuffleDiscoveryService {

    private final SpotifyClient spotifyClient;

    private static final List<String> RANDOM_TERMS = List.of(
            "a",
            "e",
            "i",
            "night",
            "mix",
            "wave",
            "love",
            "dream",
            "2024",
            "drift",
            "sad",
            "dark",
            "1"
    );

    public Playlist discoverRandomPlaylist(){

        String randomTerm = RANDOM_TERMS.get((int) (Math.random() * RANDOM_TERMS.size()));

        int randomOffset = ThreadLocalRandom.current().nextInt(0, 100);

        List<PlaylistSimplified> playlists = spotifyClient.searchPlaylists(randomTerm, randomOffset);

        List<PlaylistSimplified> filtered = playlists.stream()
                .filter(Objects::nonNull)
                .filter(p -> p.getOwner() != null)
                .filter(p -> p.getOwner().getId() != null)
                .filter(p -> !p.getOwner().getId().equalsIgnoreCase("spotify"))
                .toList();

        if (filtered.isEmpty()) {
            return null;
        }

        PlaylistSimplified randomPlaylist = filtered.get(ThreadLocalRandom.current().nextInt(filtered.size()));

        return new Playlist(randomPlaylist.getName(),
                            randomPlaylist.getOwner().getDisplayName(),
                            randomPlaylist.getExternalUrls().getExternalUrls().get("spotify"));

    }
}
