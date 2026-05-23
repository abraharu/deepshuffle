package org.example.deepshuffle.spotify.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyTokenPayload(@JsonProperty("access_token") String accessToken,
                                  @JsonProperty("refresh_token") String refreshToken,
                                  @JsonProperty("token_type") String tokenType,
                                  @JsonProperty("expires_in") long expiresIn,
                                  @JsonProperty("scope") String scope) {
}
