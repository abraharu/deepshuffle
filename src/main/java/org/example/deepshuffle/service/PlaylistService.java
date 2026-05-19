package org.example.deepshuffle.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PlaylistService {

    private final Map<String, List<String>> playlists = Map.of("test1",
            List.of("https://open.spotify.com/playlist/37i9dQZF1E4CPogRGAbHk2?si=e33ff7d4723f4398",
                    "https://open.spotify.com/playlist/37i9dQZF1EQntZpEGgfBif?si=d3b33450710e4941"),
            "test2", List.of("song3", "https://open.spotify.com/playlist/37i9dQZEVXcDtX0NCjBIqn?si=75e17715e6184030"));

    public String getRandomPlaylist(String genre){

        List<String> playlist = playlists.get(genre);

        if (playlist == null || playlist.isEmpty()) {
            return "No playlists found for genre: " + genre;
        }

        int randomIndex = ThreadLocalRandom.current().nextInt(playlist.size());

        return playlist.get(randomIndex);
    }


}
