package org.example.deepshuffle.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spotify")
public record SpotifyProperties(Client client,
                                String redirectUri,
                                OAuth oauth) {

    public record Client(String id, String secret) {
    }

    public record OAuth(String stateSecret, long stateTtlSeconds) {
    }
}
