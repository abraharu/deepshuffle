package org.example.deepshuffle.spotify.discovery.service;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.spotify.discovery.mapper.DiscoveredPlaylistMapper;
import org.example.deepshuffle.spotify.discovery.repository.DiscoveredPlaylistRepository;
import org.example.deepshuffle.spotify.dto.Playlist;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaylistLookupService {

    private static final String PLAYLIST_URL_PREFIX = "https://open.spotify.com/playlist/";

    private final DiscoveredPlaylistRepository playlistRepository;
    private final DiscoveredPlaylistMapper playlistMapper;

    public Playlist findPlaylistCard(String playlistId) {
        return playlistRepository.findBySpotifyPlaylistId(playlistId)
                .map(playlistMapper::toPlaylist)
                .orElseGet(() -> fallbackPlaylist(playlistId));
    }

    private Playlist fallbackPlaylist(String playlistId) {
        return new Playlist(
                playlistId,
                "Unknown playlist",
                "Unknown curator",
                PLAYLIST_URL_PREFIX + playlistId
        );
    }
}
