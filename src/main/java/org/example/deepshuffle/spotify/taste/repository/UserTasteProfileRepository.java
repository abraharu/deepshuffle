package org.example.deepshuffle.spotify.taste.repository;

import org.example.deepshuffle.spotify.taste.entity.UserTasteProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTasteProfileRepository extends JpaRepository<UserTasteProfileEntity, Long> {

    Optional<UserTasteProfileEntity> findByTelegramUserId(Long telegramUserId);
}
