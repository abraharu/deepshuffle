package org.example.deepshuffle.bot.message;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.bot.keyboard.PlaylistChoiceKeyboardFactory;
import org.example.deepshuffle.bot.state.UserState;
import org.example.deepshuffle.service.PlaylistVibeSearchService;
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
    private final TelegramMessageService messageService;
    private final PlaylistChoiceKeyboardFactory playlistChoiceKeyboardFactory;

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

        messageService.sendMessage(
                chatId,
                "I found 5 playlist options. Open any of them in Spotify and play the one you want."
        );

        String normalizedVibe = vibeText == null ? "" : vibeText.trim();
        for (int i = 0; i < playlists.size(); i++) {
            Playlist playlist = playlists.get(i);
            String ownerName = playlist.ownerName() == null || playlist.ownerName().isBlank()
                    ? "Unknown curator"
                    : playlist.ownerName();

            String playlistMessage = """
                    %d. %s
                    Curator: %s
                    Mood: %s
                    """
                    .formatted(i + 1, playlist.name(), ownerName, normalizedVibe);

            messageService.sendMessage(
                    chatId,
                    playlistMessage,
                    playlistChoiceKeyboardFactory.create(playlist)
            );
        }
    }
}
