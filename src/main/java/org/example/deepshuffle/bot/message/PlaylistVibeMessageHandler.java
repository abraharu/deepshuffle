package org.example.deepshuffle.bot.message;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.bot.keyboard.PlaylistVibeKeyboardFactory;
import org.example.deepshuffle.bot.state.UserState;
import org.example.deepshuffle.service.PlaylistVibeSearchService;
import org.example.deepshuffle.service.PlaylistVibeSessionService;
import org.example.deepshuffle.service.TelegramMessageService;
import org.example.deepshuffle.service.UserStateService;
import org.example.deepshuffle.spotify.dto.Playlist;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PlaylistVibeMessageHandler {

    private final UserStateService userStateService;
    private final PlaylistVibeSearchService playlistVibeSearchService;
    private final PlaylistVibeSessionService sessionService;
    private final TelegramMessageService messageService;
    private final PlaylistVibeKeyboardFactory keyboardFactory;

    public boolean supports(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }

        Long chatId = update.getMessage().getChatId();
        return userStateService.getState(chatId) == UserState.WAITING_FOR_PLAYLIST_VIBE;
    }

    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String vibeText = update.getMessage().getText();

        List<Playlist> playlists = playlistVibeSearchService.searchPlaylistsByVibe(vibeText);
        if (playlists.isEmpty()) {
            messageService.sendMessage(
                    chatId,
                    "I couldn't find playlist options for that mood. Send another description and I'll search again."
            );
            return;
        }

        userStateService.clearState(chatId);
        String normalizedVibe = vibeText == null || vibeText.isBlank() ? "chill music" : vibeText.trim();
        sessionService.save(chatId, normalizedVibe, playlists);

        messageService.sendMessage(
                chatId,
                """
                Playlist mood: %s

                I found %d playlists. Tap one to open its details.
                """.formatted(normalizedVibe, playlists.size()),
                keyboardFactory.list(playlists)
        );
    }
}
