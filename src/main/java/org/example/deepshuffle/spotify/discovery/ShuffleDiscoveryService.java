package org.example.deepshuffle.spotify.discovery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.deepshuffle.spotify.client.SpotifyClient;
import org.example.deepshuffle.spotify.dto.Playlist;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
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


    public Playlist discoverRandomPlaylist() {

        for (int i = 0; i < 5; i++) {

            Playlist playlist = null;

            try {
                playlist = tryDiscoverRandomPlaylist();
            } catch (Exception e) {
                log.warn("Failed to discover random playlist on attempt {}: {}", i + 1, e.getMessage());
            }

            if (playlist != null) {

                return playlist;
            }
        }

        return null;
    }

    private Playlist tryDiscoverRandomPlaylist(){
        String randomTerm = RANDOM_TERMS.get(ThreadLocalRandom.current().nextInt(RANDOM_TERMS.size()));

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

        try {
            List<PlaylistTrack> tracks = spotifyClient.getPlaylistsTrack(randomPlaylist.getId());

            if (tracks.isEmpty()) {
                return mapPlaylist(randomPlaylist);
            }

            PlaylistTrack randomTrack = tracks.get(ThreadLocalRandom.current().nextInt(tracks.size()));

            if (!(randomTrack.getTrack() instanceof Track track)) {
                return mapPlaylist(randomPlaylist);
            }

            ArtistSimplified[] artistSimplifieds = track.getArtists();

            if (artistSimplifieds.length == 0) {
                return mapPlaylist(randomPlaylist);
            }

            ArtistSimplified artist = artistSimplifieds[ThreadLocalRandom.current().nextInt(artistSimplifieds.length)];

            List<PlaylistSimplified> playlistsByArtist = spotifyClient.searchPlaylists(artist.getName(),
                    ThreadLocalRandom.current().nextInt(0, 50));


            List<PlaylistSimplified> filteredPlaylist = playlistsByArtist.stream()
                    .filter(Objects::nonNull)
                    .filter(playlistSimplified -> playlistSimplified.getOwner() != null)
                    .filter(playlistSimplified -> playlistSimplified.getOwner().getId() != null)
                    .filter(playlistSimplified -> !playlistSimplified.getOwner()
                            .getId()
                            .equalsIgnoreCase("spotify"))
                    .toList();

            if (filteredPlaylist.isEmpty()) {
                return mapPlaylist(randomPlaylist);
            }

            PlaylistSimplified finalPlaylist = filteredPlaylist.get(ThreadLocalRandom.current().nextInt(filteredPlaylist.size()));

            return mapPlaylist(finalPlaylist);
        } catch (Exception e) {
            log.warn("Traversal failed for playlist {}: {}", randomPlaylist.getId(), e.getMessage());
            return mapPlaylist(randomPlaylist);
        }

    }

    private Playlist mapPlaylist(PlaylistSimplified playlist) {
        return new Playlist(playlist.getId(),
                playlist.getName(),
                playlist.getOwner().getDisplayName(),
                playlist.getExternalUrls()
                        .getExternalUrls()
                        .get("spotify"));
    }
}
