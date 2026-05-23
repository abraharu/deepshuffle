package org.example.deepshuffle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

@Service
@RequiredArgsConstructor
public class SpotifyAuthService {

    private final SpotifyApi spotifyApi;

    public String getAccessToken(){
        try {
            ClientCredentialsRequest request = spotifyApi.clientCredentials().build();
            ClientCredentials credentials = request.execute();

            return credentials.getAccessToken();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        throw new RuntimeException("Failed to get access token");
    }
}
