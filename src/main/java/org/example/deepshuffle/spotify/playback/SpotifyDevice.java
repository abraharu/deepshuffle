package org.example.deepshuffle.spotify.playback;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyDevice(String id,
                            @JsonProperty("is_active") boolean isActive,
                            String name,
                            String type) {
}
