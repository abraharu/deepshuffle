package org.example.deepshuffle.spotify;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

import java.util.Arrays;
import java.util.List;

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
}
