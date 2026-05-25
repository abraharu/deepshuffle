package org.example.deepshuffle.bot.callback;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.bot.auth.SpotifyAuthMessageFormatter;
import org.example.deepshuffle.bot.keyboard.SpotifyAuthKeyboardFactory;
import org.example.deepshuffle.service.SpotifyOAuthService;
import org.example.deepshuffle.service.TelegramMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class ReconnectSpotifyCallbackHandler implements CallbackHandler {

    private static final String CALLBACK = "reconnect_spotify";

    private final SpotifyOAuthService oauthService;
    private final TelegramMessageService messageService;
    private final SpotifyAuthMessageFormatter messageFormatter;
    private final SpotifyAuthKeyboardFactory keyboardFactory;

    @Override
    public boolean supports(String callback) {
        return CALLBACK.equals(callback);
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long telegramUserId = update.getCallbackQuery().getFrom().getId();
        String loginUrl = oauthService.generateLoginUrl(telegramUserId);

        messageService.answerCallback(update.getCallbackQuery().getId(), "Spotify reconnect link is ready");
        messageService.sendMessage(
                chatId,
                messageFormatter.reconnectRequired(),
                keyboardFactory.reconnect(loginUrl)
        );
    }
}
