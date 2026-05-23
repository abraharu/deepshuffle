package org.example.deepshuffle.persistence.repository;

import org.example.deepshuffle.persistence.entity.SpotifyUserTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpotifyUserTokenRepository extends JpaRepository<SpotifyUserTokenEntity, Long> {

    Optional<SpotifyUserTokenEntity> findByTelegramUserId(Long telegramUserId);
}
