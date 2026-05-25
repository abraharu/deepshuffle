package org.example.deepshuffle.bot.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.deepshuffle.bot.keyboard.SpotifyAuthKeyboardFactory;
import org.example.deepshuffle.bot.keyboard.TasteKeyboardFactory;
import org.example.deepshuffle.bot.taste.TasteMessageFormatter;
import org.example.deepshuffle.service.SpotifyOAuthService;
import org.example.deepshuffle.service.TelegramMessageService;
import org.example.deepshuffle.spotify.auth.exception.SpotifyAuthorizationRequiredException;
import org.example.deepshuffle.spotify.taste.model.UserTasteSnapshot;
import org.example.deepshuffle.spotify.taste.service.UserTasteProfileService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class SyncTasteCallbackHandler implements CallbackHandler {

    private static final String CALLBACK = "sync_taste";

    private final UserTasteProfileService tasteProfileService;
    private final SpotifyOAuthService oauthService;
    private final TelegramMessageService messageService;
    private final TasteMessageFormatter messageFormatter;
    private final SpotifyAuthKeyboardFactory authKeyboardFactory;
    private final TasteKeyboardFactory tasteKeyboardFactory;

    @Override
    public boolean supports(String callback) {
        return CALLBACK.equals(callback);
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long telegramUserId = update.getCallbackQuery().getFrom().getId();

        try {
            messageService.answerCallback(update.getCallbackQuery().getId(), "Syncing taste fingerprint");
            messageService.sendMessage(chatId, messageFormatter.loading());

            UserTasteSnapshot snapshot = tasteProfileService.syncTasteSnapshot(telegramUserId);
            messageService.sendMessage(
                    chatId,
                    messageFormatter.success(snapshot),
                    tasteKeyboardFactory.syncResult()
            );
        } catch (SpotifyAuthorizationRequiredException e) {
            String loginUrl = oauthService.generateLoginUrl(telegramUserId);
            messageService.sendMessage(
                    chatId,
                    messageFormatter.authRequired(loginUrl),
                    authKeyboardFactory.reconnect(loginUrl)
            );
        } catch (Exception e) {
            log.warn("Failed to sync taste fingerprint for Telegram user {}: {}", telegramUserId, e.getMessage());
            messageService.sendMessage(
                    chatId,
                    messageFormatter.failure(),
                    authKeyboardFactory.reconnectAction()
            );
        }
    }
}
