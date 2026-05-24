package org.example.deepshuffle.spotify.discovery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.deepshuffle.spotify.client.SpotifyClient;
import org.example.deepshuffle.spotify.discovery.mapper.DiscoveredPlaylistMapper;
import org.example.deepshuffle.spotify.discovery.model.DiscoveredPlaylist;
import org.example.deepshuffle.spotify.discovery.seed.RandomQueryGenerator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotifyPlaylistCrawler {

    private static final int SEARCH_LIMIT = 10;

    private final RandomQueryGenerator randomQueryGenerator;
    private final SpotifyClient spotifyClient;
    private final DiscoveredPlaylistMapper playlistMapper;
    private final PlaylistPersistenceService playlistPersistenceService;

    @Scheduled(fixedDelay = 3000)
    public void crawl() {
        String query = randomQueryGenerator.randomQuery();
        int offset = ThreadLocalRandom.current().nextInt(0, 1000);

        try {
            List<DiscoveredPlaylist> playlists = spotifyClient.searchPlaylists(query, offset, SEARCH_LIMIT).stream()
                    .filter(this::isValidPlaylist)
                    .map(playlist -> playlistMapper.fromSpotify(playlist, query))
                    .toList();

            if (!playlists.isEmpty()) {
                playlistPersistenceService.saveAll(playlists);
            }
        } catch (Exception e) {
            log.warn("Spotify playlist crawl failed for query '{}' offset {}: {}", query, offset, e.getMessage());
        }
    }

    private boolean isValidPlaylist(PlaylistSimplified playlist) {
        return playlist != null
                && playlist.getId() != null
                && playlist.getName() != null
                && playlist.getOwner() != null
                && playlist.getOwner().getId() != null
                && !playlist.getOwner().getId().equalsIgnoreCase("spotify")
                && playlist.getExternalUrls() != null
                && playlist.getExternalUrls().getExternalUrls() != null
                && Objects.nonNull(playlist.getExternalUrls().getExternalUrls().get("spotify"));
    }
}
