package org.example.deepshuffle.spotify.taste.dto;

import java.util.List;

public record SpotifySavedTracksResponse(List<SpotifySavedTrackItem> items,
                                         Integer total,
                                         String next) {
}
