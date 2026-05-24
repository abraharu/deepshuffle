package org.example.deepshuffle.spotify.taste.client;

import org.example.deepshuffle.spotify.taste.dto.SpotifyArtistItem;
import org.example.deepshuffle.spotify.taste.dto.SpotifySavedTracksResponse;
import org.example.deepshuffle.spotify.taste.dto.SpotifyTopItemsResponse;
import org.example.deepshuffle.spotify.taste.dto.SpotifyTrackItem;
import org.example.deepshuffle.spotify.taste.model.TasteTimeRange;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class SpotifyTasteClient {

    private static final String API_BASE_URL = "https://api.spotify.com/v1";
    private static final int MAX_PAGE_LIMIT = 50;

    private final WebClient webClient = WebClient.builder()
            .baseUrl(API_BASE_URL)
            .build();

    public List<SpotifyArtistItem> getTopArtists(String accessToken, TasteTimeRange timeRange, int limit) {
        SpotifyTopItemsResponse<SpotifyArtistItem> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/me/top/artists")
                        .queryParam("time_range", timeRange.spotifyValue())
                        .queryParam("limit", Math.min(limit, MAX_PAGE_LIMIT))
                        .build())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SpotifyTopItemsResponse<SpotifyArtistItem>>() {
                })
                .block();

        return response == null || response.items() == null ? List.of() : response.items();
    }

    public List<SpotifyTrackItem> getTopTracks(String accessToken, TasteTimeRange timeRange, int limit) {
        SpotifyTopItemsResponse<SpotifyTrackItem> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/me/top/tracks")
                        .queryParam("time_range", timeRange.spotifyValue())
                        .queryParam("limit", Math.min(limit, MAX_PAGE_LIMIT))
                        .build())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SpotifyTopItemsResponse<SpotifyTrackItem>>() {
                })
                .block();

        return response == null || response.items() == null ? List.of() : response.items();
    }

    public List<SpotifySavedTracksResponse> getSavedTracksPages(String accessToken, int maxTracks) {
        int remaining = maxTracks;
        int offset = 0;
        List<SpotifySavedTracksResponse> pages = new java.util.ArrayList<>();

        while (remaining > 0) {
            int limit = Math.min(remaining, MAX_PAGE_LIMIT);
            int pageOffset = offset;
            SpotifySavedTracksResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/me/tracks")
                            .queryParam("limit", limit)
                            .queryParam("offset", pageOffset)
                            .build())
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .bodyToMono(SpotifySavedTracksResponse.class)
                    .block();

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
}
