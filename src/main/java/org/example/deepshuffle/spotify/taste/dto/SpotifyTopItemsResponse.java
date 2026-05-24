package org.example.deepshuffle.spotify.taste.dto;

import java.util.List;

public record SpotifyTopItemsResponse<T>(List<T> items) {
}
