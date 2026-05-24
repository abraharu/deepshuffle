package org.example.deepshuffle.bot.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.deepshuffle.bot.keyboard.PlaybackKeyboardFactory;
import org.example.deepshuffle.bot.playback.PlaybackMessageFormatter;
import org.example.deepshuffle.service.SpotifyOAuthService;
import org.example.deepshuffle.service.SpotifyPlaybackService;
import org.example.deepshuffle.service.TelegramMessageService;
import org.example.deepshuffle.spotify.auth.exception.SpotifyAuthorizationRequiredException;
import org.example.deepshuffle.spotify.auth.exception.SpotifyPlaybackException;
import org.example.deepshuffle.spotify.discovery.service.PlaylistLookupService;
import org.example.deepshuffle.spotify.dto.Playlist;
import org.example.deepshuffle.spotify.playback.SpotifyPlaybackResult;
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
    private final PlaybackMessageFormatter messageFormatter;
    private final PlaybackKeyboardFactory keyboardFactory;
    private final PlaylistLookupService playlistLookupService;

    @Override
    public boolean supports(String callback) {
        return callback.startsWith(CALLBACK_PREFIX);
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        Long telegramUserId = update.getCallbackQuery().getFrom().getId();
        String playlistId = update.getCallbackQuery().getData().substring(CALLBACK_PREFIX.length());
        Playlist playlist = playlistLookupService.findPlaylistCard(playlistId);

        try {
            messageService.answerCallback(update.getCallbackQuery().getId(), "Starting Spotify playback");
            messageService.editMessage(
                    chatId,
                    messageId,
                    messageFormatter.loading(playlist),
                    keyboardFactory.loading(playlistId)
            );

            SpotifyPlaybackResult result = playbackService.playPlaylist(telegramUserId, playlistId);
            messageService.editMessage(
                    chatId,
                    messageId,
                    messageFormatter.success(result, playlist),
                    keyboardFactory.success(result.playlistId())
            );
        } catch (SpotifyAuthorizationRequiredException e) {
            String loginUrl = oauthService.generateLoginUrl(telegramUserId);
            messageService.editMessage(
                    chatId,
                    messageId,
                    messageFormatter.authRequired(loginUrl),
                    keyboardFactory.authRequired(loginUrl, playlistId)
            );
        } catch (SpotifyPlaybackException e) {
            messageService.editMessage(
                    chatId,
                    messageId,
                    messageFormatter.failure(playlist, e),
                    keyboardFactory.failure(playlistId)
            );
        } catch (Exception e) {
            log.warn("Failed to start Spotify playback for Telegram user {}: {}", telegramUserId, e.getMessage());
            messageService.editMessage(
                    chatId,
                    messageId,
                    messageFormatter.unexpectedFailure(playlist),
                    keyboardFactory.failure(playlistId)
            );
        }
    }
}
