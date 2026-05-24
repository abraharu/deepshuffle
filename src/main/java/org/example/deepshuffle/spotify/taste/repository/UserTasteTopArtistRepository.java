package org.example.deepshuffle.spotify.taste.repository;

import org.example.deepshuffle.spotify.taste.entity.UserTasteTopArtistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTasteTopArtistRepository extends JpaRepository<UserTasteTopArtistEntity, Long> {

    void deleteByTelegramUserIdAndTimeRange(Long telegramUserId, String timeRange);
}
