package org.example.deepshuffle.spotify.discovery.model;

import lombok.Builder;

import java.time.Instant;

@Builder
public record DiscoveredPlaylist(String spotifyPlaylistId,
                                 String name,
                                 String ownerName,
                                 String spotifyUrl,
                                 String querySeed,
                                 Integer usageCount,
                                 Instant discoveredAt,
                                 Instant lastServedAt,
                                 String language,
                                 Boolean active) {
}
