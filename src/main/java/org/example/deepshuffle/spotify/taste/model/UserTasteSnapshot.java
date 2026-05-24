package org.example.deepshuffle.spotify.taste.model;

import lombok.Builder;

@Builder
public record UserTasteSnapshot(Long telegramUserId,
                                int topArtistsCount,
                                int topTracksCount,
                                int likedTracksCount,
                                int randomnessLevel) {
}
