package org.example.deepshuffle.service;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.spotify.playback.SpotifyDevice;
import org.example.deepshuffle.spotify.playback.SpotifyDeviceService;
import org.example.deepshuffle.spotify.auth.exception.SpotifyPlaybackException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpotifyPlaybackService {

    private static final String API_BASE_URL = "https://api.spotify.com/v1";
    private static final String OPEN_SPOTIFY_MESSAGE = "Open Spotify on one of your devices first";

    private final SpotifyTokenService tokenService;
    private final SpotifyDeviceService deviceService;
    private final WebClient webClient = WebClient.builder()
            .baseUrl(API_BASE_URL)
            .build();

    public String playPlaylist(Long telegramUserId, String playlistUri) {
        String accessToken = tokenService.getValidAccessToken(telegramUserId);

        try {
            return playPlaylistWithToken(accessToken, playlistUri);
        } catch (WebClientResponseException.Unauthorized e) {
            String refreshedToken = tokenService.findByTelegramUserId(telegramUserId)
                    .map(tokenService::refreshAccessToken)
                    .map(token -> token.getAccessToken())
                    .orElseThrow();
            try {
                return playPlaylistWithToken(refreshedToken, playlistUri);
            } catch (WebClientResponseException refreshedException) {
                throw playbackException(refreshedException);
            }
        } catch (WebClientResponseException e) {
            throw playbackException(e);
        }
    }

    private String playPlaylistWithToken(String accessToken, String playlistUri) {
        Optional<SpotifyDevice> device = deviceService.findPreferredDevice(accessToken);

        if (device.isEmpty()) {
            throw new SpotifyPlaybackException(OPEN_SPOTIFY_MESSAGE);
        }

        webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/me/player/play")
                        .queryParam("device_id", device.get().id())
                        .build())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("context_uri", normalizePlaylistUri(playlistUri)))
                .retrieve()
                .toBodilessEntity()
                .block();

        return "Playback started";
    }

    private SpotifyPlaybackException playbackException(WebClientResponseException e) {
        return switch (e.getStatusCode().value()) {
            case 403 -> new SpotifyPlaybackException("Spotify Premium is required for remote playback", e);
            case 404 -> new SpotifyPlaybackException(OPEN_SPOTIFY_MESSAGE, e);
            case 429 -> new SpotifyPlaybackException("Spotify rate limit reached. Try again in a minute", e);
            default -> new SpotifyPlaybackException("Spotify playback is unavailable right now", e);
        };
    }

    private String normalizePlaylistUri(String playlistUri) {
        if (playlistUri.startsWith("spotify:playlist:")) {
            return playlistUri;
        }
        return "spotify:playlist:" + playlistUri;
    }
}
