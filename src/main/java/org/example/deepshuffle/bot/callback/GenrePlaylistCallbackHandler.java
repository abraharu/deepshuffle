package org.example.deepshuffle.bot.callback;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.service.PlaylistService;
import org.example.deepshuffle.service.TelegramMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
@RequiredArgsConstructor
public class GenrePlaylistCallbackHandler implements CallbackHandler{

    private final PlaylistService playlistService;

    private final TelegramMessageService messageService;

    @Override
    public boolean supports(String callback) {
        return callback.startsWith("genre:");
    }

    @Override
    public void handle(Update update) {

        String data = update.getCallbackQuery().getData();

        String genre = data.split(":")[1];

        String playlist = playlistService.getRandomPlaylist(genre);

        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        if(playlist == null){
            messageService.sendMessage(chatId, "No playlists found for genre: " + genre);

            return;
        }
        
        messageService.sendMessage(chatId, playlist);

    }
}
