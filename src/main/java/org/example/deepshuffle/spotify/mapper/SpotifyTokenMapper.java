package org.example.deepshuffle.spotify.mapper;

import org.example.deepshuffle.persistence.entity.SpotifyUserTokenEntity;
import org.example.deepshuffle.spotify.auth.model.SpotifyUserToken;
import org.springframework.stereotype.Component;

@Component
public class SpotifyTokenMapper {

    public SpotifyUserToken toModel(SpotifyUserTokenEntity entity) {
        if (entity == null) {
            return null;
        }

        return SpotifyUserToken.builder()
                .telegramUserId(entity.getTelegramUserId())
                .spotifyUserId(entity.getSpotifyUserId())
                .accessToken(entity.getAccessToken())
                .refreshToken(entity.getRefreshToken())
                .expiresAt(entity.getExpiresAt())
                .build();
    }

    public SpotifyUserTokenEntity toEntity(SpotifyUserToken token) {
        if (token == null) {
            return null;
        }

        return SpotifyUserTokenEntity.builder()
                .telegramUserId(token.getTelegramUserId())
                .spotifyUserId(token.getSpotifyUserId())
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .expiresAt(token.getExpiresAt())
                .build();
    }

    public void updateEntity(SpotifyUserToken token, SpotifyUserTokenEntity entity) {
        entity.setTelegramUserId(token.getTelegramUserId());
        entity.setSpotifyUserId(token.getSpotifyUserId());
        entity.setAccessToken(token.getAccessToken());
        entity.setRefreshToken(token.getRefreshToken());
        entity.setExpiresAt(token.getExpiresAt());
    }
}
