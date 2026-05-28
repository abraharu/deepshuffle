package org.example.deepshuffle.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.deepshuffle.spotify.client.SpotifyClient;
import org.example.deepshuffle.spotify.discovery.mapper.DiscoveredPlaylistMapper;
import org.example.deepshuffle.spotify.discovery.model.DiscoveredPlaylist;
import org.example.deepshuffle.spotify.discovery.service.PlaylistPersistenceService;
import org.example.deepshuffle.spotify.dto.Playlist;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaylistVibeSearchService {

    private static final int SEARCH_LIMIT = 10;
    private static final int RESULT_LIMIT = 5;

    private final SpotifyClient spotifyClient;
    private final PlaylistPersistenceService playlistPersistenceService;
    private final DiscoveredPlaylistMapper playlistMapper;

    public List<Playlist> searchPlaylistsByVibe(String vibeText) {
        String query = normalizeQuery(vibeText);

        try {
            List<DiscoveredPlaylist> discoveredPlaylists = spotifyClient.searchPlaylists(query, 0, SEARCH_LIMIT).stream()
                    .filter(this::isValidPlaylist)
                    .map(playlist -> playlistMapper.fromSpotify(playlist, query))
                    .limit(RESULT_LIMIT)
                    .toList();

            if (discoveredPlaylists.isEmpty()) {
                return cachedResults(query);
            }

            List<DiscoveredPlaylist> persistedPlaylists = playlistPersistenceService.saveAll(discoveredPlaylists);
            return persistedPlaylists.stream()
                    .map(this::toPlaylist)
                    .toList();
        } catch (Exception e) {
            log.warn("Spotify vibe search failed for query '{}': {}", query, e.getMessage());
            return cachedResults(query);
        }
    }

    private Playlist toPlaylist(DiscoveredPlaylist discoveredPlaylist) {
        return new Playlist(
                discoveredPlaylist.spotifyPlaylistId(),
                discoveredPlaylist.name(),
                discoveredPlaylist.ownerName(),
                discoveredPlaylist.spotifyUrl()
        );
    }

    private boolean isValidPlaylist(PlaylistSimplified playlistSimplified) {
        return playlistSimplified != null
                && playlistSimplified.getId() != null
                && playlistSimplified.getName() != null
                && playlistSimplified.getExternalUrls() != null
                && playlistSimplified.getExternalUrls().getExternalUrls() != null
                && Objects.nonNull(playlistSimplified.getExternalUrls().getExternalUrls().get("spotify"));
    }

    private String normalizeQuery(String vibeText) {
        if (vibeText == null || vibeText.isBlank()) {
            return "chill music";
        }
        return vibeText.trim();
    }

    private List<Playlist> cachedResults(String query) {
        return playlistPersistenceService.findCachedByQuery(query, RESULT_LIMIT).stream()
                .map(this::toPlaylist)
                .toList();
    }
}
