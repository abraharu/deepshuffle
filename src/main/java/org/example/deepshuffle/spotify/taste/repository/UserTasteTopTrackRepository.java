package org.example.deepshuffle.spotify.taste.repository;

import org.example.deepshuffle.spotify.taste.entity.UserTasteTopTrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTasteTopTrackRepository extends JpaRepository<UserTasteTopTrackEntity, Long> {

    void deleteByTelegramUserIdAndTimeRange(Long telegramUserId, String timeRange);
}
