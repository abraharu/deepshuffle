package org.example.deepshuffle.spotify.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyUserToken {

    private Long telegramUserId;
    private String spotifyUserId;
    private String accessToken;
    private String refreshToken;
    private Instant expiresAt;

}
