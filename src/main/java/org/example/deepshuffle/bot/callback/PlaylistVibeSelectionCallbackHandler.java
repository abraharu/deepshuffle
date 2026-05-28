package org.example.deepshuffle.bot.callback;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.bot.keyboard.PlaylistVibeKeyboardFactory;
import org.example.deepshuffle.service.PlaylistVibeSessionService;
import org.example.deepshuffle.service.TelegramMessageService;
import org.example.deepshuffle.spotify.dto.Playlist;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class PlaylistVibeSelectionCallbackHandler implements CallbackHandler {

    private static final String VIEW_PLAYLIST_PREFIX = "view_vibe_playlist:";
    private static final String BACK_TO_LIST_CALLBACK = "back:vibe_list";

    private final TelegramMessageService messageService;
    private final PlaylistVibeSessionService sessionService;
    private final PlaylistVibeKeyboardFactory keyboardFactory;

    @Override
    public boolean supports(String callback) {
        return callback.startsWith(VIEW_PLAYLIST_PREFIX) || callback.equals(BACK_TO_LIST_CALLBACK);
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        String callback = update.getCallbackQuery().getData();

        if (callback.equals(BACK_TO_LIST_CALLBACK)) {
            sessionService.find(chatId).ifPresentOrElse(
                    session -> {
                        messageService.answerCallback(update.getCallbackQuery().getId(), "Back to playlist list");
                        messageService.editMessage(
                                chatId,
                                messageId,
                                formatListMessage(session.vibeText(), session.playlists().size()),
                                keyboardFactory.list(session.playlists())
                        );
                    },
                    () -> messageService.answerCallback(update.getCallbackQuery().getId(), "Playlist list expired")
            );
            return;
        }

        String playlistId = callback.substring(VIEW_PLAYLIST_PREFIX.length());
        sessionService.findPlaylist(chatId, playlistId).ifPresentOrElse(
                playlist -> {
                    messageService.answerCallback(update.getCallbackQuery().getId(), "Playlist selected");
                    messageService.editMessage(
                            chatId,
                            messageId,
                            formatDetailMessage(playlist),
                            keyboardFactory.detail(playlist)
                    );
                },
                () -> messageService.answerCallback(update.getCallbackQuery().getId(), "Playlist not found")
        );
    }

    private String formatListMessage(String vibeText, int size) {
        return """
                Playlist mood: %s

                I found %d playlists. Tap one to open its details.
                """.formatted(vibeText, size);
    }

    private String formatDetailMessage(Playlist playlist) {
        String ownerName = playlist.ownerName() == null || playlist.ownerName().isBlank()
                ? "Unknown curator"
                : playlist.ownerName();

        return """
                %s

                Curator: %s
                %s
                """.formatted(playlist.name(), ownerName, playlist.url());
    }
}
