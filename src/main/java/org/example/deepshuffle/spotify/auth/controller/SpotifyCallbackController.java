package org.example.deepshuffle.spotify.auth.controller;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.service.SpotifyOAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class SpotifyCallbackController {

    private final SpotifyOAuthService spotifyOAuthService;

    @GetMapping("/spotify/callback")
    public ResponseEntity<String> callback(@RequestParam(required = false) String code,
                                           @RequestParam(required = false) String state,
                                           @RequestParam(required = false) String error) {
        if (error != null && !error.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Spotify authorization failed: " + error);
        }
        if (code == null || code.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Spotify authorization code is missing");
        }

        Long telegramUserId = spotifyOAuthService.validateState(state);
        spotifyOAuthService.exchangeCode(telegramUserId, code);
        return ResponseEntity.ok("Spotify account connected. Return to Telegram and continue in DeepShuffle.");
    }
}
