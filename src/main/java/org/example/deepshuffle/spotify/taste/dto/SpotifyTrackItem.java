package org.example.deepshuffle.spotify.taste.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SpotifyTrackItem(String id,
                               String name,
                               List<SpotifySimplifiedArtistItem> artists,
                               SpotifyAlbumItem album,
                               Integer popularity,
                               @JsonProperty("explicit") Boolean explicitTrack,
                               @JsonProperty("external_urls")
                               SpotifyExternalUrls externalUrls) {
}
