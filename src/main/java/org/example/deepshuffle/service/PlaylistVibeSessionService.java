package org.example.deepshuffle.service;

import org.example.deepshuffle.spotify.dto.Playlist;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlaylistVibeSessionService {

    private final Map<Long, PlaylistVibeSession> sessions = new ConcurrentHashMap<>();

    public void save(Long chatId, String vibeText, List<Playlist> playlists) {
        sessions.put(chatId, new PlaylistVibeSession(vibeText, List.copyOf(playlists)));
    }

    public Optional<PlaylistVibeSession> find(Long chatId) {
        return Optional.ofNullable(sessions.get(chatId));
    }

    public Optional<Playlist> findPlaylist(Long chatId, String playlistId) {
        return find(chatId)
                .flatMap(session -> session.playlists().stream()
                        .filter(playlist -> playlist.id().equals(playlistId))
                        .findFirst());
    }

    public boolean containsPlaylist(Long chatId, String playlistId) {
        return findPlaylist(chatId, playlistId).isPresent();
    }

    public record PlaylistVibeSession(String vibeText, List<Playlist> playlists) {
    }
}
