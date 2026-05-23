package org.example.deepshuffle.bot.callback;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.service.ShuffleDiscoveryService;
import org.example.deepshuffle.service.TelegramMessageService;
import org.example.deepshuffle.spotify.dto.Playlist;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
@RequiredArgsConstructor
public class ShuffleAgainCallbackHandler implements CallbackHandler{

    private final ShuffleDiscoveryService shuffleDiscoveryService;
    private final TelegramMessageService messageService;

    @Override
    public boolean supports(String callback) {
        return callback.equals("shuffle");
    }

    @Override
    public void handle(Update update) {

         Playlist playlist = shuffleDiscoveryService.discoverRandomPlaylist();

         Long chatId = update.getCallbackQuery().getMessage().getChatId();

         if (playlist == null) {

             messageService.sendMessage(chatId, "No playlists found");
             return;

         }

        String response = """
        🎧 %s
        
        👤 %s
        
        %s
        """
        .formatted(playlist.name(), playlist.owner(), playlist.url());

         InlineKeyboardButton openButton = InlineKeyboardButton.builder()
                 .text("OpenSpotify")
                 .url(playlist.url())
                 .build();

        InlineKeyboardButton playButton = InlineKeyboardButton.builder()
                .text("\u25B6 Play on Desktop")
                .callbackData("play_playlist:" + playlist.id())
                .build();

        InlineKeyboardButton shuffleButton = InlineKeyboardButton.builder()
                 .text("Shuffle")
                 .callbackData("shuffle")
                 .build();

        InlineKeyboardButton backToMenu = InlineKeyboardButton.builder()
                .text("Back to menu")
                .callbackData("back_to_menu")
                .build();

         InlineKeyboardRow row1 = new InlineKeyboardRow();
         row1.add(openButton);

         InlineKeyboardRow row2 = new InlineKeyboardRow();
         row2.add(playButton);

        InlineKeyboardRow row3 = new InlineKeyboardRow();
        row3.add(shuffleButton);

        InlineKeyboardRow row4 = new InlineKeyboardRow();
        row4.add(backToMenu);

         InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                 .keyboardRow(row1)
                 .keyboardRow(row2)
                 .keyboardRow(row3)
                 .keyboardRow(row4)
                 .build();

         messageService.sendMessage(chatId, response, markup);
    }
}
