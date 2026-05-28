package org.example.deepshuffle.spotify.discovery.mapper;

import org.example.deepshuffle.spotify.discovery.entity.DiscoveredPlaylistEntity;
import org.example.deepshuffle.spotify.discovery.model.DiscoveredPlaylist;
import org.example.deepshuffle.spotify.dto.Playlist;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

import java.time.Instant;
import java.util.Map;

@Component
public class DiscoveredPlaylistMapper {

    public DiscoveredPlaylist fromSpotify(PlaylistSimplified playlist, String querySeed) {
        return DiscoveredPlaylist.builder()
                .spotifyPlaylistId(playlist.getId())
                .name(playlist.getName())
                .ownerName(resolveOwnerName(playlist))
                .spotifyUrl(extractSpotifyUrl(playlist))
                .querySeed(querySeed)
                .usageCount(0)
                .discoveredAt(Instant.now())
                .language(detectLanguage(playlist.getName()))
                .active(true)
                .build();
    }

    public DiscoveredPlaylist toModel(DiscoveredPlaylistEntity entity) {
        return DiscoveredPlaylist.builder()
                .spotifyPlaylistId(entity.getSpotifyPlaylistId())
                .name(entity.getName())
                .ownerName(entity.getOwnerName())
                .spotifyUrl(entity.getSpotifyUrl())
                .querySeed(entity.getQuerySeed())
                .usageCount(entity.getUsageCount())
                .discoveredAt(entity.getDiscoveredAt())
                .lastServedAt(entity.getLastServedAt())
                .language(entity.getLanguage())
                .active(entity.getActive())
                .build();
    }

    public Playlist toPlaylist(DiscoveredPlaylistEntity entity) {
        return new Playlist(
                entity.getSpotifyPlaylistId(),
                entity.getName(),
                entity.getOwnerName(),
                entity.getSpotifyUrl()
        );
    }

    public DiscoveredPlaylistEntity toEntity(DiscoveredPlaylist playlist) {
        return DiscoveredPlaylistEntity.builder()
                .spotifyPlaylistId(playlist.spotifyPlaylistId())
                .name(playlist.name())
                .ownerName(playlist.ownerName())
                .spotifyUrl(playlist.spotifyUrl())
                .querySeed(playlist.querySeed())
                .usageCount(playlist.usageCount())
                .discoveredAt(playlist.discoveredAt())
                .lastServedAt(playlist.lastServedAt())
                .language(playlist.language())
                .active(playlist.active())
                .build();
    }

    public void updateEntity(DiscoveredPlaylist playlist, DiscoveredPlaylistEntity entity) {
        entity.setName(playlist.name());
        entity.setOwnerName(playlist.ownerName());
        entity.setSpotifyUrl(playlist.spotifyUrl());
        entity.setQuerySeed(playlist.querySeed());
        entity.setLanguage(playlist.language());
        entity.setActive(playlist.active());
    }

    private String extractSpotifyUrl(PlaylistSimplified playlist) {
        if (playlist.getExternalUrls() == null || playlist.getExternalUrls().getExternalUrls() == null) {
            return null;
        }

        Map<String, String> urls = playlist.getExternalUrls().getExternalUrls();
        return urls.get("spotify");
    }

    private String resolveOwnerName(PlaylistSimplified playlist) {
        if (playlist.getOwner() == null) {
            return "Unknown curator";
        }

        if (playlist.getOwner().getDisplayName() != null && !playlist.getOwner().getDisplayName().isBlank()) {
            return playlist.getOwner().getDisplayName();
        }

        if (playlist.getOwner().getId() != null && !playlist.getOwner().getId().isBlank()) {
            return playlist.getOwner().getId();
        }

        return "Unknown curator";
    }

    private String detectLanguage(String value) {
        if (value != null && value.matches(".*\\p{IsCyrillic}.*")) {
            return "ru";
        }

        return "unknown";
    }
}
