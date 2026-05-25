package org.example.deepshuffle.spotify.discovery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.deepshuffle.spotify.client.SpotifyClient;
import org.example.deepshuffle.spotify.client.SpotifyRateLimitException;
import org.example.deepshuffle.spotify.discovery.mapper.DiscoveredPlaylistMapper;
import org.example.deepshuffle.spotify.discovery.model.DiscoveredPlaylist;
import org.example.deepshuffle.spotify.discovery.seed.RandomQueryGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotifyPlaylistCrawler {

    private static final int SEARCH_LIMIT = 10;
    private static final int DEFAULT_RATE_LIMIT_COOLDOWN_SECONDS = 60;
    private static final int MAX_RATE_LIMIT_COOLDOWN_SECONDS = 15 * 60;

    private final RandomQueryGenerator randomQueryGenerator;
    private final SpotifyClient spotifyClient;
    private final DiscoveredPlaylistMapper playlistMapper;
    private final PlaylistPersistenceService playlistPersistenceService;

    @Value("${deepshuffle.discovery.crawler.enabled:true}")
    private boolean enabled;

    private Instant rateLimitedUntil = Instant.EPOCH;

    @Scheduled(
            fixedDelayString = "${deepshuffle.discovery.crawler.fixed-delay-ms:30000}",
            initialDelayString = "${deepshuffle.discovery.crawler.initial-delay-ms:10000}"
    )
    public void crawl() {
        if (!enabled) {
            return;
        }

        if (isCoolingDown()) {
            return;
        }

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
        } catch (SpotifyRateLimitException e) {
            applyRateLimitCooldown(e);
        } catch (Exception e) {
            log.warn("Spotify playlist crawl failed for query '{}' offset {}: {}", query, offset, e.getMessage());
        }
    }

    private boolean isCoolingDown() {
        return Instant.now().isBefore(rateLimitedUntil);
    }

    private void applyRateLimitCooldown(SpotifyRateLimitException e) {
        int retryAfterSeconds = e.getRetryAfterSeconds() > 0
                ? e.getRetryAfterSeconds()
                : DEFAULT_RATE_LIMIT_COOLDOWN_SECONDS;
        int cooldownSeconds = Math.min(retryAfterSeconds, MAX_RATE_LIMIT_COOLDOWN_SECONDS);

        rateLimitedUntil = Instant.now().plusSeconds(cooldownSeconds);
        log.warn(
                "Spotify crawler rate-limited. Pausing crawl for {} seconds until {}",
                cooldownSeconds,
                rateLimitedUntil
        );
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
