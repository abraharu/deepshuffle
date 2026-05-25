package org.example.deepshuffle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.michaelthelin.spotify.SpotifyApi;

@Configuration
public class SpotifyConfig {

    @Bean
    public SpotifyApi spotifyApi(SpotifyProperties spotifyProperties) {

        return new SpotifyApi.Builder()
                .setClientId(spotifyProperties.client().id())
                .setClientSecret(spotifyProperties.client().secret())
                .build();
    }
}
