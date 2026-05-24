package org.example.deepshuffle.service;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.spotify.auth.exception.SpotifyPlaybackException;
import org.example.deepshuffle.spotify.playback.SpotifyDevice;
import org.example.deepshuffle.spotify.playback.SpotifyDeviceService;
import org.example.deepshuffle.spotify.playback.SpotifyPlaybackError;
import org.example.deepshuffle.spotify.playback.SpotifyPlaybackResult;
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
    private static final String PLAYLIST_URI_PREFIX = "spotify:playlist:";
    private static final String PLAYLIST_URL_PREFIX = "https://open.spotify.com/playlist/";

    private final SpotifyTokenService tokenService;
    private final SpotifyDeviceService deviceService;
    private final WebClient webClient = WebClient.builder()
            .baseUrl(API_BASE_URL)
            .build();

    public SpotifyPlaybackResult playPlaylist(Long telegramUserId, String playlistId) {
        String accessToken = tokenService.getValidAccessToken(telegramUserId);

        try {
            return playPlaylistWithToken(accessToken, playlistId);
        } catch (WebClientResponseException.Unauthorized e) {
            String refreshedToken = tokenService.findByTelegramUserId(telegramUserId)
                    .map(tokenService::refreshAccessToken)
                    .map(token -> token.getAccessToken())
                    .orElseThrow();
            try {
                return playPlaylistWithToken(refreshedToken, playlistId);
            } catch (WebClientResponseException refreshedException) {
                throw playbackException(refreshedException);
            }
        } catch (WebClientResponseException e) {
            throw playbackException(e);
        }
    }

    private SpotifyPlaybackResult playPlaylistWithToken(String accessToken, String playlistId) {
        Optional<SpotifyDevice> device = deviceService.findPreferredDevice(accessToken);

        if (device.isEmpty()) {
            throw new SpotifyPlaybackException(SpotifyPlaybackError.DEVICE_NOT_FOUND);
        }

        String playlistUri = normalizePlaylistUri(playlistId);
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

        return new SpotifyPlaybackResult(
                extractPlaylistId(playlistUri),
                playlistUri,
                playlistUrl(playlistUri),
                device.get(),
                "Playback started"
        );
    }

    private SpotifyPlaybackException playbackException(WebClientResponseException e) {
        SpotifyPlaybackError error = SpotifyPlaybackError.fromStatus(e.getStatusCode().value());
        return new SpotifyPlaybackException(error, e);
    }

    private String normalizePlaylistUri(String playlistUri) {
        if (playlistUri.startsWith(PLAYLIST_URI_PREFIX)) {
            return playlistUri;
        }
        return PLAYLIST_URI_PREFIX + playlistUri;
    }

    private String extractPlaylistId(String playlistUri) {
        return playlistUri.replace(PLAYLIST_URI_PREFIX, "");
    }

    private String playlistUrl(String playlistUri) {
        return PLAYLIST_URL_PREFIX + extractPlaylistId(playlistUri);
    }
}
