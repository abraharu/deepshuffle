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
        name = "user_taste_profiles",
        indexes = {
                @Index(name = "idx_user_taste_profiles_telegram_user_id", columnList = "telegramUserId", unique = true),
                @Index(name = "idx_user_taste_profiles_last_synced_at", columnList = "lastSyncedAt")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTasteProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long telegramUserId;

    private String spotifyUserId;

    @Column(nullable = false)
    private Integer randomnessLevel;

    private Instant createdAt;

    private Instant updatedAt;

    private Instant lastSyncedAt;
}
