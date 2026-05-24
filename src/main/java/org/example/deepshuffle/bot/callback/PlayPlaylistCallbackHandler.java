package org.example.deepshuffle.bot.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.deepshuffle.service.SpotifyOAuthService;
import org.example.deepshuffle.service.SpotifyPlaybackService;
import org.example.deepshuffle.service.TelegramMessageService;
import org.example.deepshuffle.spotify.auth.exception.SpotifyAuthorizationRequiredException;
import org.example.deepshuffle.spotify.auth.exception.SpotifyPlaybackException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlayPlaylistCallbackHandler implements CallbackHandler {

    private static final String CALLBACK_PREFIX = "play_playlist:";

    private final SpotifyPlaybackService playbackService;
    private final SpotifyOAuthService oauthService;
    private final TelegramMessageService messageService;

    @Override
    public boolean supports(String callback) {
        return callback.startsWith(CALLBACK_PREFIX);
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long telegramUserId = update.getCallbackQuery().getFrom().getId();
        String playlistId = update.getCallbackQuery().getData().substring(CALLBACK_PREFIX.length());

        try {
            messageService.answerCallback(update.getCallbackQuery().getId(), "Starting Spotify playback");
            String message = playbackService.playPlaylist(telegramUserId, playlistId);
            messageService.sendMessage(chatId, message);
        } catch (SpotifyAuthorizationRequiredException e) {
            String loginUrl = oauthService.generateLoginUrl(telegramUserId);
            messageService.sendMessage(chatId, "Connect Spotify first:\n" + loginUrl);
        } catch (SpotifyPlaybackException e) {
            messageService.sendMessage(chatId, e.getMessage());
        } catch (Exception e) {
            log.warn("Failed to start Spotify playback for Telegram user {}: {}", telegramUserId, e.getMessage());
            messageService.sendMessage(chatId, "Could not start Spotify playback");
        }
    }
}
