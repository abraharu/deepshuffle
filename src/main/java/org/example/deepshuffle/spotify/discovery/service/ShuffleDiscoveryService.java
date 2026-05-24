package org.example.deepshuffle.spotify.discovery.service;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.spotify.dto.Playlist;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShuffleDiscoveryService {

    private final PlaylistRandomizationService playlistRandomizationService;

    public Playlist discoverRandomPlaylist() {
        return playlistRandomizationService.weightedRandomPlaylist()
                .orElse(null);
    }
}
