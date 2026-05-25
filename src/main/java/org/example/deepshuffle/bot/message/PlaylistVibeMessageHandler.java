package org.example.deepshuffle.bot.message;

import lombok.RequiredArgsConstructor;
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

    public boolean supports(Update update){
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }

        Long chatId = update.getMessage().getChatId();
        return userStateService.getState(chatId) == UserState.WAITING_FOR_PLAYLIST_VIBE;
    }

    public void handle(Update update){
        Long chatId = update.getMessage().getChatId();
        String vibeText = update.getMessage().getText();

        userStateService.clearState(chatId);

        List<Playlist> playlists = playlistVibeSearchService.searchPlaylistsByVibe(vibeText);

        if (playlists.isEmpty()) {
            messageService.sendMessage(chatId, "I couldn't find playlists for this vibe. Try describing it differently."
            );
            return;
        }

        StringBuilder playlistMessage = new StringBuilder("Here are 5 playlists for your vibe 🎧\n\n");

        for (int i = 0; i < 5 && i < playlists.size(); i++) {
            Playlist playlist = playlists.get(i);
            playlistMessage.append(i + 1)
                    .append(". ")
                    .append(playlist.name())
                    .append("\n")
                    .append("By")
                    .append(playlist.ownerName())
                    .append("\n")
                    .append(playlist.url())
                    .append("\n\n");
        }

        messageService.sendMessage(chatId, playlistMessage.toString());
    }
}
