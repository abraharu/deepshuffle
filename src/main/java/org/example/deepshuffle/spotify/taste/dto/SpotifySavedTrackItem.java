package org.example.deepshuffle.spotify.taste.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record SpotifySavedTrackItem(@JsonProperty("added_at") Instant addedAt,
                                    SpotifyTrackItem track) {
}
