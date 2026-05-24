package org.example.deepshuffle.spotify.taste.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "user_taste_top_tracks",
        indexes = {
                @Index(name = "idx_user_taste_top_tracks_user_range", columnList = "telegramUserId,timeRange"),
                @Index(name = "idx_user_taste_top_tracks_track_id", columnList = "trackId"),
                @Index(name = "idx_user_taste_top_tracks_primary_artist_id", columnList = "primaryArtistId")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTasteTopTrackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long telegramUserId;

    @Column(nullable = false)
    private String timeRange;

    @Column(nullable = false)
    private Integer rankPosition;

    @Column(nullable = false)
    private String trackId;

    private String trackName;

    private String primaryArtistId;

    private String primaryArtistName;

    private String albumName;

    private Integer popularity;

    private Boolean explicitTrack;

    private Instant syncedAt;
}
