package org.example.deepshuffle.spotify.discovery.service;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.spotify.discovery.entity.DiscoveredPlaylistEntity;
import org.example.deepshuffle.spotify.discovery.mapper.DiscoveredPlaylistMapper;
import org.example.deepshuffle.spotify.discovery.repository.DiscoveredPlaylistRepository;
import org.example.deepshuffle.spotify.dto.Playlist;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PlaylistRandomizationService {

    private static final int CANDIDATE_LIMIT = 100;

    private final DiscoveredPlaylistRepository playlistRepository;
    private final DiscoveredPlaylistMapper playlistMapper;

    @Transactional
    public Optional<Playlist> weightedRandomPlaylist() {
        List<DiscoveredPlaylistEntity> candidates = playlistRepository.findWeightedCandidates(
                PageRequest.of(0, CANDIDATE_LIMIT)
        );

        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        DiscoveredPlaylistEntity selected = selectWeighted(candidates);
        selected.setUsageCount(safeInt(selected.getUsageCount()) + 1);
        selected.setLastServedAt(Instant.now());

        return Optional.of(playlistMapper.toPlaylist(playlistRepository.save(selected)));
    }

    private DiscoveredPlaylistEntity selectWeighted(List<DiscoveredPlaylistEntity> candidates) {
        int totalWeight = candidates.stream()
                .mapToInt(this::weight)
                .sum();
        int roll = ThreadLocalRandom.current().nextInt(Math.max(totalWeight, 1));

        int cursor = 0;
        for (DiscoveredPlaylistEntity candidate : candidates) {
            cursor += weight(candidate);
            if (roll < cursor) {
                return candidate;
            }
        }

        return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
    }

    private int weight(DiscoveredPlaylistEntity playlist) {
        int usagePenalty = safeInt(playlist.getUsageCount()) * 12;
        int freshBoost = freshBoost(playlist.getDiscoveredAt());
        int neverServedBoost = playlist.getLastServedAt() == null ? 100 : 0;
        int staleBoost = staleBoost(playlist.getLastServedAt());

        return Math.max(5, 20 + freshBoost + neverServedBoost + staleBoost - usagePenalty);
    }

    private int freshBoost(Instant discoveredAt) {
        if (discoveredAt == null) {
            return 0;
        }

        long hours = Duration.between(discoveredAt, Instant.now()).toHours();
        return hours < 24 ? 40 : 0;
    }

    private int staleBoost(Instant lastServedAt) {
        if (lastServedAt == null) {
            return 0;
        }

        long hours = Duration.between(lastServedAt, Instant.now()).toHours();
        return (int) Math.min(hours * 2, 60);
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }
}
