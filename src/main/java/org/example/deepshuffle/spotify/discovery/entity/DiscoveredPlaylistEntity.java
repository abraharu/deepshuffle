package org.example.deepshuffle.spotify.discovery.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "discovered_playlists")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscoveredPlaylistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String spotifyPlaylistId;

    private String name;

    private String ownerName;

    private String spotifyUrl;

    private String querySeed;

    private Integer usageCount;

    private Instant discoveredAt;

    private Instant lastServedAt;

    private String language;

    private Boolean active;
}
