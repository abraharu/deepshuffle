package org.example.deepshuffle.spotify.playback;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class SpotifyDeviceService {

    private static final String API_BASE_URL = "https://api.spotify.com/v1";
    private static final String COMPUTER_DEVICE_TYPE = "Computer";

    private final WebClient webClient = WebClient.builder()
            .baseUrl(API_BASE_URL)
            .build();

    public Optional<SpotifyDevice> findPreferredDevice(String accessToken) {
        SpotifyDevicesResponse devicesResponse = webClient.get()
                .uri("/me/player/devices")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(SpotifyDevicesResponse.class)
                .block();

        return selectPreferredDevice(
                devicesResponse == null ? List.of() : devicesResponse.devices()
        );
    }

    public Optional<SpotifyDevice> selectPreferredDevice(List<SpotifyDevice> devices) {
        if (devices == null || devices.isEmpty()) {
            return Optional.empty();
        }

        return devices.stream()
                .filter(this::canPlay)
                .max(Comparator.comparingInt(this::priority));
    }

    private boolean canPlay(SpotifyDevice device) {
        return device != null
                && device.id() != null
                && !device.id().isBlank()
                && !device.isRestricted();
    }

    private int priority(SpotifyDevice device) {
        if (device.isActive()) {
            return 3;
        }
        if (COMPUTER_DEVICE_TYPE.equalsIgnoreCase(device.type())) {
            return 2;
        }
        return 1;
    }
}
