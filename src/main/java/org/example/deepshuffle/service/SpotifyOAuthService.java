package org.example.deepshuffle.service;

import org.example.deepshuffle.config.SpotifyProperties;
import org.example.deepshuffle.spotify.auth.dto.SpotifyMeResponse;
import org.example.deepshuffle.spotify.auth.dto.SpotifyTokenPayload;
import org.example.deepshuffle.spotify.auth.exception.SpotifyAuthorizationRequiredException;
import org.example.deepshuffle.spotify.auth.model.SpotifyUserToken;
import org.example.deepshuffle.spotify.auth.service.SpotifyOAuthStateService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;

@Service
public class SpotifyOAuthService {

    private static final String AUTHORIZE_URL = "https://accounts.spotify.com/authorize";
    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";
    private static final String ME_URL = "https://api.spotify.com/v1/me";

    private final WebClient webClient;
    private final SpotifyTokenService tokenService;
    private final SpotifyProperties spotifyProperties;
    private final SpotifyOAuthStateService stateService;

    public SpotifyOAuthService(SpotifyTokenService tokenService,
                               SpotifyProperties spotifyProperties,
                               SpotifyOAuthStateService stateService) {
        this.webClient = WebClient.builder().build();
        this.tokenService = tokenService;
        this.spotifyProperties = spotifyProperties;
        this.stateService = stateService;
    }

    public String generateLoginUrl(Long telegramUserId) {
        return UriComponentsBuilder.fromUriString(AUTHORIZE_URL)
                .queryParam("client_id", spotifyProperties.client().id())
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", spotifyProperties.redirectUri())
                .queryParam("scope", scopes())
                .queryParam("state", stateService.generateState(telegramUserId))
                .build()
                .encode()
                .toUriString();
    }

    public SpotifyUserToken exchangeCode(Long telegramUserId, String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", code);
        form.add("redirect_uri", spotifyProperties.redirectUri());
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

        if (payload == null || payload.accessToken() == null || payload.refreshToken() == null) {
            throw new SpotifyAuthorizationRequiredException("Spotify did not return the required tokens");
        }

        SpotifyMeResponse me = webClient.get()
                .uri(ME_URL)
                .headers(headers -> headers.setBearerAuth(payload.accessToken()))
                .retrieve()
                .bodyToMono(SpotifyMeResponse.class)
                .block();

        String spotifyUserId = me == null ? null : me.id();
        SpotifyUserToken token = SpotifyUserToken.builder()
                .telegramUserId(telegramUserId)
                .spotifyUserId(spotifyUserId)
                .accessToken(payload.accessToken())
                .refreshToken(payload.refreshToken())
                .expiresAt(Instant.now().plusSeconds(payload.expiresIn()))
                .build();

        return tokenService.save(token);
    }

    public Long validateState(String state) {
        return stateService.validateAndExtractTelegramUserId(state);
    }

    private String scopes() {
        return String.join(" ",
                "user-modify-playback-state",
                "user-read-playback-state",
                "user-read-currently-playing",
                "user-top-read",
                "user-library-read"
        );
    }
}
