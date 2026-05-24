package org.example.deepshuffle.spotify.taste.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SpotifyArtistItem(String id,
                                String name,
                                Integer popularity,
                                List<String> genres,
                                @JsonProperty("external_urls")
                                SpotifyExternalUrls externalUrls) {
}
