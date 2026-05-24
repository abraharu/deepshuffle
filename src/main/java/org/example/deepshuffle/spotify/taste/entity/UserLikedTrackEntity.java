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
        name = "user_liked_tracks",
        indexes = {
                @Index(name = "idx_user_liked_tracks_user_track", columnList = "telegramUserId,trackId", unique = true),
                @Index(name = "idx_user_liked_tracks_primary_artist_id", columnList = "primaryArtistId"),
                @Index(name = "idx_user_liked_tracks_added_at", columnList = "addedAt")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLikedTrackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long telegramUserId;

    @Column(nullable = false)
    private String trackId;

    private String trackName;

    private String primaryArtistId;

    private String primaryArtistName;

    private String albumName;

    private Integer popularity;

    private Boolean explicitTrack;

    private Instant addedAt;

    private Instant syncedAt;
}
