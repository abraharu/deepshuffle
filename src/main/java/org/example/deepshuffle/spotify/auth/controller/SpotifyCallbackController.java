package org.example.deepshuffle.spotify.auth.controller;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.spotify.auth.service.SpotifyOAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SpotifyCallbackController {

    private final SpotifyOAuthService spotifyOAuthService;

    @GetMapping("/spotify/callback")
    public ResponseEntity<String> callback(@RequestParam String code,
                                           @RequestParam String state) {
        Long telegramUserId = Long.parseLong(state);
        spotifyOAuthService.exchangeCode(telegramUserId, code);
        return ResponseEntity.ok("Spotify account connected. You can return to Telegram and press Play on Desktop.");
    }
}
