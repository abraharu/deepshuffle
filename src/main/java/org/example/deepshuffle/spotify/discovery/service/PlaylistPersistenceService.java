package org.example.deepshuffle.spotify.discovery.service;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.spotify.discovery.entity.DiscoveredPlaylistEntity;
import org.example.deepshuffle.spotify.discovery.mapper.DiscoveredPlaylistMapper;
import org.example.deepshuffle.spotify.discovery.model.DiscoveredPlaylist;
import org.example.deepshuffle.spotify.discovery.repository.DiscoveredPlaylistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistPersistenceService {

    private final DiscoveredPlaylistRepository playlistRepository;
    private final DiscoveredPlaylistMapper playlistMapper;

    @Transactional
    public List<DiscoveredPlaylist> saveAll(List<DiscoveredPlaylist> playlists) {
        return playlists.stream()
                .map(this::save)
                .toList();
    }

    @Transactional
    public DiscoveredPlaylist save(DiscoveredPlaylist playlist) {
        DiscoveredPlaylistEntity entity = playlistRepository
                .findBySpotifyPlaylistId(playlist.spotifyPlaylistId())
                .orElseGet(() -> {
                    DiscoveredPlaylistEntity newEntity = playlistMapper.toEntity(playlist);
                    newEntity.setUsageCount(0);
                    newEntity.setDiscoveredAt(Instant.now());
                    return newEntity;
                });

        playlistMapper.updateEntity(playlist, entity);
        if (entity.getActive() == null) {
            entity.setActive(true);
        }
        if (entity.getUsageCount() == null) {
            entity.setUsageCount(0);
        }
        if (entity.getDiscoveredAt() == null) {
            entity.setDiscoveredAt(Instant.now());
        }

        return playlistMapper.toModel(playlistRepository.save(entity));
    }

    public boolean hasEnoughPlaylists(int minimumSize) {
        return playlistRepository.countByActiveTrue() >= minimumSize;
    }
}
