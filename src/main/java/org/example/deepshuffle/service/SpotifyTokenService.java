package org.example.deepshuffle.service;

import org.example.deepshuffle.config.SpotifyProperties;
import org.example.deepshuffle.persistence.entity.SpotifyUserTokenEntity;
import org.example.deepshuffle.persistence.repository.SpotifyUserTokenRepository;
import org.example.deepshuffle.spotify.auth.dto.SpotifyTokenPayload;
import org.example.deepshuffle.spotify.auth.exception.SpotifyAuthorizationRequiredException;
import org.example.deepshuffle.spotify.auth.exception.SpotifyTokenRefreshException;
import org.example.deepshuffle.spotify.auth.model.SpotifyUserToken;
import org.example.deepshuffle.spotify.mapper.SpotifyTokenMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.Optional;

@Service
public class SpotifyTokenService {

    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";

    private final SpotifyUserTokenRepository tokenRepository;
    private final SpotifyTokenMapper tokenMapper;
    private final WebClient webClient = WebClient.builder().build();
    private final SpotifyProperties spotifyProperties;

    public SpotifyTokenService(SpotifyUserTokenRepository tokenRepository,
                               SpotifyTokenMapper tokenMapper,
                               SpotifyProperties spotifyProperties) {
        this.tokenRepository = tokenRepository;
        this.tokenMapper = tokenMapper;
        this.spotifyProperties = spotifyProperties;
    }

    public Optional<SpotifyUserToken> findByTelegramUserId(Long telegramUserId) {
        return tokenRepository.findByTelegramUserId(telegramUserId)
                .map(tokenMapper::toModel);
    }

    public SpotifyUserToken save(SpotifyUserToken token) {
        SpotifyUserTokenEntity entity = tokenRepository.findByTelegramUserId(token.getTelegramUserId())
                .orElseGet(() -> tokenMapper.toEntity(token));

        tokenMapper.updateEntity(token, entity);

        return tokenMapper.toModel(tokenRepository.save(entity));
    }

    public String getValidAccessToken(Long telegramUserId) {
        SpotifyUserToken token = findByTelegramUserId(telegramUserId)
                .orElseThrow(() -> new SpotifyAuthorizationRequiredException("Spotify account is not connected"));

        if (token.getExpiresAt() != null && token.getExpiresAt().isAfter(Instant.now().plusSeconds(60))) {
            return token.getAccessToken();
        }

        return refreshAccessToken(token).getAccessToken();
    }

    public SpotifyUserToken refreshAccessToken(SpotifyUserToken token) {
        if (token.getRefreshToken() == null || token.getRefreshToken().isBlank()) {
            throw new SpotifyAuthorizationRequiredException("Spotify refresh token is missing");
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", token.getRefreshToken());

        SpotifyTokenPayload payload = webClient.post()
                .uri(TOKEN_URL)
                .headers(headers -> headers.setBasicAuth(
                        spotifyProperties.client().id(),
                        spotifyProperties.client().secret()
                ))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(SpotifyTokenPayload.class)
                .block();

        if (payload == null || payload.accessToken() == null) {
            throw new SpotifyTokenRefreshException("Failed to refresh Spotify access token");
        }

        token.setAccessToken(payload.accessToken());
        if (payload.refreshToken() != null && !payload.refreshToken().isBlank()) {
            token.setRefreshToken(payload.refreshToken());
        }
        token.setExpiresAt(Instant.now().plusSeconds(payload.expiresIn()));
        return save(token);
    }
}
