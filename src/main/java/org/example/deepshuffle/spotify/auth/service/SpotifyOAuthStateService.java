package org.example.deepshuffle.spotify.auth.service;

import org.example.deepshuffle.config.SpotifyProperties;
import org.example.deepshuffle.spotify.auth.exception.SpotifyAuthorizationRequiredException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class SpotifyOAuthStateService {

    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final SpotifyProperties spotifyProperties;

    public SpotifyOAuthStateService(SpotifyProperties spotifyProperties) {
        this.spotifyProperties = spotifyProperties;
    }

    public String generateState(Long telegramUserId) {
        long issuedAt = Instant.now().getEpochSecond();
        String nonce = randomNonce();
        String payload = telegramUserId + ":" + issuedAt + ":" + nonce;
        String signature = sign(payload);
        return ENCODER.encodeToString((payload + ":" + signature).getBytes(StandardCharsets.UTF_8));
    }

    public Long validateAndExtractTelegramUserId(String state) {
        if (state == null || state.isBlank()) {
            throw new SpotifyAuthorizationRequiredException("Spotify OAuth state is missing");
        }

        final String decoded;
        try {
            decoded = new String(DECODER.decode(state), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new SpotifyAuthorizationRequiredException("Spotify OAuth state is malformed");
        }
        String[] parts = decoded.split(":");
        if (parts.length != 4) {
            throw new SpotifyAuthorizationRequiredException("Spotify OAuth state is invalid");
        }

        String payload = parts[0] + ":" + parts[1] + ":" + parts[2];
        String expectedSignature = sign(payload);
        if (!expectedSignature.equals(parts[3])) {
            throw new SpotifyAuthorizationRequiredException("Spotify OAuth state signature is invalid");
        }

        long issuedAt = Long.parseLong(parts[1]);
        long ttlSeconds = spotifyProperties.oauth().stateTtlSeconds();
        if (Instant.now().isAfter(Instant.ofEpochSecond(issuedAt + ttlSeconds))) {
            throw new SpotifyAuthorizationRequiredException("Spotify OAuth state expired");
        }

        return Long.parseLong(parts[0]);
    }

    private String sign(String payload) {
        if (!StringUtils.hasText(spotifyProperties.oauth().stateSecret())) {
            throw new IllegalStateException("Spotify OAuth state secret is not configured");
        }
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(
                    spotifyProperties.oauth().stateSecret().getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            );
            mac.init(secretKey);
            return ENCODER.encodeToString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to sign Spotify OAuth state", e);
        }
    }

    private String randomNonce() {
        byte[] bytes = new byte[12];
        SECURE_RANDOM.nextBytes(bytes);
        return ENCODER.encodeToString(bytes);
    }
}
