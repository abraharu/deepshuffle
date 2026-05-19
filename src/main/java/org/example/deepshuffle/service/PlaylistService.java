package org.example.deepshuffle.service;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.spotify.SpotifyClient;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final SpotifyClient spotifyClient;

    public String getRandomPlaylist(String genre){

        List<PlaylistSimplified> playlist = spotifyClient.searchPlaylists(genre);

        if (playlist == null || playlist.isEmpty()) {
            return "No playlists found for genre: " + genre;
        }

        return playlist.get(ThreadLocalRandom.current().nextInt(playlist.size())).getName();
    }


}
