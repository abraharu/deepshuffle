package org.example.deepshuffle.service;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.spotify.playback.SpotifyDevice;
import org.example.deepshuffle.spotify.playback.SpotifyDevicesResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpotifyPlaybackService {

    private static final String API_BASE_URL = "https://api.spotify.com/v1";
    private static final String OPEN_SPOTIFY_MESSAGE = "Open Spotify on one of your devices first";

    private final SpotifyTokenService tokenService;
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
            return playPlaylistWithToken(refreshedToken, playlistUri);
        }
    }

    private String playPlaylistWithToken(String accessToken, String playlistUri) {
        SpotifyDevicesResponse devicesResponse = webClient.get()
                .uri("/me/player/devices")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(SpotifyDevicesResponse.class)
                .block();

        Optional<SpotifyDevice> activeDevice = Optional.ofNullable(devicesResponse)
                .map(SpotifyDevicesResponse::devices)
                .stream()
                .flatMap(List::stream)
                .filter(SpotifyDevice::isActive)
                .findFirst();

        if (activeDevice.isEmpty()) {
            return OPEN_SPOTIFY_MESSAGE;
        }

        webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/me/player/play")
                        .queryParam("device_id", activeDevice.get().id())
                        .build())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("context_uri", normalizePlaylistUri(playlistUri)))
                .retrieve()
                .toBodilessEntity()
                .block();

        return "Playback started";
    }

    private String normalizePlaylistUri(String playlistUri) {
        if (playlistUri.startsWith("spotify:playlist:")) {
            return playlistUri;
        }
        return "spotify:playlist:" + playlistUri;
    }
}
