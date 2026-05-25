package org.example.deepshuffle.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpotifyAuthService {

    private final SpotifyApi spotifyApi;

    public String getAccessToken(){
        try {
            ClientCredentialsRequest request = spotifyApi.clientCredentials().build();
            ClientCredentials credentials = request.execute();

            return credentials.getAccessToken();
        } catch (Exception e) {
            log.warn("Failed to get Spotify client credentials token: {}", e.getMessage());
        }
        throw new RuntimeException("Failed to get access token");
    }
}
