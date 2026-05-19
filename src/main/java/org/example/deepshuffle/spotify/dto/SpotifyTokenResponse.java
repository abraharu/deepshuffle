package org.example.deepshuffle.spotify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyTokenResponse(@JsonProperty("acess_token")
                                   String acessToken,
                                   @JsonProperty("token_type")
                                   String tokenType,
                                   @JsonProperty("expires_in")
                                   int expiresIn) {
}
