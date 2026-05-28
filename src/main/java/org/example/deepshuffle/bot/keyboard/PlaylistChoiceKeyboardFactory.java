package org.example.deepshuffle.bot.keyboard;

import org.example.deepshuffle.spotify.dto.Playlist;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class PlaylistChoiceKeyboardFactory {

    public InlineKeyboardMarkup create(Playlist playlist) {
        InlineKeyboardRow row1 = new InlineKeyboardRow();
        row1.add(openButton(playlist.url()));
        row1.add(playButton(playlist.id()));

        InlineKeyboardRow row2 = new InlineKeyboardRow();
        row2.add(backButton());

        return InlineKeyboardMarkup.builder()
                .keyboardRow(row1)
                .keyboardRow(row2)
                .build();
    }

    private InlineKeyboardButton openButton(String url) {
        return InlineKeyboardButton.builder()
                .text("Open")
                .url(url)
                .build();
    }

    private InlineKeyboardButton playButton(String playlistId) {
        return InlineKeyboardButton.builder()
                .text("Play")
                .callbackData("play_playlist:" + playlistId)
                .build();
    }

    private InlineKeyboardButton backButton() {
        return InlineKeyboardButton.builder()
                .text("Back")
                .callbackData("back:main")
                .build();
    }
}
