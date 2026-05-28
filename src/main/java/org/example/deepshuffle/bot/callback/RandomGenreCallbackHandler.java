package org.example.deepshuffle.bot.callback;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.bot.state.UserState;
import org.example.deepshuffle.service.TelegramMessageService;
import org.example.deepshuffle.service.UserStateService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class RandomGenreCallbackHandler implements CallbackHandler {

    private static final String LEGACY_CALLBACK = "random_genre";
    private static final String PLAYLIST_VIBE_CALLBACK = "playlist_vibe";

    private final TelegramMessageService messageService;
    private final UserStateService userStateService;

    @Override
    public boolean supports(String callback) {
        return callback.startsWith(LEGACY_CALLBACK) || callback.startsWith(PLAYLIST_VIBE_CALLBACK);
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        userStateService.setState(chatId, UserState.WAITING_FOR_PLAYLIST_VIBE);

        messageService.sendMessage(
                chatId,
                """
                Describe the playlist mood you want to hear.

                For example:
                - dark techno for night coding
                - chill indie for walking
                - sad acoustic songs
                - energetic gym rap
                - dreamy summer house
                """
        );
    }
}
