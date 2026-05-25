package org.example.deepshuffle.spotify.taste.client;

import org.example.deepshuffle.spotify.taste.dto.SpotifyArtistItem;
import org.example.deepshuffle.spotify.taste.dto.SpotifySavedTracksResponse;
import org.example.deepshuffle.spotify.taste.dto.SpotifyTopItemsResponse;
import org.example.deepshuffle.spotify.taste.dto.SpotifyTrackItem;
import org.example.deepshuffle.spotify.taste.model.TasteTimeRange;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

@Component
public class SpotifyTasteClient {

    private static final String API_BASE_URL = "https://api.spotify.com/v1";
    private static final int MAX_PAGE_LIMIT = 50;
    private static final int MAX_ATTEMPTS = 3;
    private static final Duration DEFAULT_RETRY_DELAY = Duration.ofSeconds(2);
    private static final Duration MAX_RETRY_DELAY = Duration.ofSeconds(10);

    private final WebClient webClient = WebClient.builder()
            .baseUrl(API_BASE_URL)
            .build();

    public List<SpotifyArtistItem> getTopArtists(String accessToken, TasteTimeRange timeRange, int limit) {
        SpotifyTopItemsResponse<SpotifyArtistItem> response = executeWithRetry(() -> webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/me/top/artists")
                                .queryParam("time_range", timeRange.spotifyValue())
                                .queryParam("limit", Math.min(limit, MAX_PAGE_LIMIT))
                                .build())
                        .headers(headers -> headers.setBearerAuth(accessToken))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<SpotifyTopItemsResponse<SpotifyArtistItem>>() {
                        })
                        .block()
        );

        return response == null || response.items() == null ? List.of() : response.items();
    }

    public List<SpotifyTrackItem> getTopTracks(String accessToken, TasteTimeRange timeRange, int limit) {
        SpotifyTopItemsResponse<SpotifyTrackItem> response = executeWithRetry(() -> webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/me/top/tracks")
                                .queryParam("time_range", timeRange.spotifyValue())
                                .queryParam("limit", Math.min(limit, MAX_PAGE_LIMIT))
                                .build())
                        .headers(headers -> headers.setBearerAuth(accessToken))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<SpotifyTopItemsResponse<SpotifyTrackItem>>() {
                        })
                        .block()
        );

        return response == null || response.items() == null ? List.of() : response.items();
    }

    public List<SpotifySavedTracksResponse> getSavedTracksPages(String accessToken, int maxTracks) {
        int remaining = maxTracks;
        int offset = 0;
        List<SpotifySavedTracksResponse> pages = new java.util.ArrayList<>();

        while (remaining > 0) {
            int limit = Math.min(remaining, MAX_PAGE_LIMIT);
            int pageOffset = offset;
            SpotifySavedTracksResponse response = executeWithRetry(() -> webClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/me/tracks")
                                    .queryParam("limit", limit)
                                    .queryParam("offset", pageOffset)
                                    .build())
                            .headers(headers -> headers.setBearerAuth(accessToken))
                            .retrieve()
                            .bodyToMono(SpotifySavedTracksResponse.class)
                            .block()
            );

            if (response == null || response.items() == null || response.items().isEmpty()) {
                break;
            }

            pages.add(response);
            remaining -= response.items().size();
            offset += response.items().size();

            if (response.next() == null || response.next().isBlank()) {
                break;
            }
        }

        return pages;
    }

    private <T> T executeWithRetry(Supplier<T> request) {
        WebClientResponseException lastException = null;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                return request.get();
            } catch (WebClientResponseException e) {
                if (!shouldRetry(e) || attempt == MAX_ATTEMPTS) {
                    throw e;
                }

                lastException = e;
                sleep(retryDelay(e));
            }
        }

        throw lastException;
    }

    private boolean shouldRetry(WebClientResponseException e) {
        int status = e.getStatusCode().value();
        return status == 429 || status == 500 || status == 502 || status == 503 || status == 504;
    }

    private Duration retryDelay(WebClientResponseException e) {
        String retryAfter = e.getHeaders().getFirst(HttpHeaders.RETRY_AFTER);
        if (retryAfter == null || retryAfter.isBlank()) {
            return DEFAULT_RETRY_DELAY;
        }

        try {
            long seconds = Long.parseLong(retryAfter);
            return Duration.ofSeconds(Math.max(1, Math.min(seconds, MAX_RETRY_DELAY.toSeconds())));
        } catch (NumberFormatException ignored) {
            return DEFAULT_RETRY_DELAY;
        }
    }

    private void sleep(Duration delay) {
        try {
            Thread.sleep(delay.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Spotify taste sync retry was interrupted", e);
        }
    }
}
