package org.example.deepshuffle.spotify.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.deepshuffle.service.SpotifyAuthService;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpotifyClient {

    private final SpotifyAuthService spotifyAuthService;

    private final SpotifyApi spotifyApi;

    public List<PlaylistSimplified> searchPlaylists(String genre){

        try {
            String accessToken = spotifyAuthService.getAccessToken();

            spotifyApi.setAccessToken(accessToken);

            Paging<PlaylistSimplified> playlists = spotifyApi.searchPlaylists(genre)
                    .build()
                    .execute();

            return Arrays.asList(playlists.getItems());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<PlaylistSimplified> searchPlaylists(String query, int offset) {
        return searchPlaylists(query, offset, 10);
    }

    public List<PlaylistSimplified> searchPlaylists(String query, int offset, int limit) {

        try {
            String accessToken = spotifyAuthService.getAccessToken();

            spotifyApi.setAccessToken(accessToken);

            Paging<PlaylistSimplified> playlists = spotifyApi.searchPlaylists(query)
                    .limit(limit)
                    .offset(offset)
                    .build()
                    .execute();
            log.info("Playlists found: {}", playlists.getItems().length);
            return Arrays.asList(playlists.getItems());

        } catch (Exception e) {
            log.error("Error searching playlists: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<PlaylistTrack> getPlaylistsTrack(String playlistId) {

        try {

            spotifyApi.setAccessToken(spotifyAuthService.getAccessToken());

            Paging<PlaylistTrack> tracks = spotifyApi.getPlaylistsItems(playlistId)
                            .limit(50)
                            .build()
                            .execute();

            return Arrays.asList(tracks.getItems());

        } catch (Exception e) {
            log.warn(
                    "Failed to fetch playlist tracks for playlist {}: {}",
                    playlistId,
                    e.getMessage()
            );

            return Collections.emptyList();
        }
    }
}
