package org.example.deepshuffle.bot.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class PlaybackKeyboardFactory {

    private static final String PLAYLIST_URL_PREFIX = "https://open.spotify.com/playlist/";

    public InlineKeyboardMarkup loading(String playlistId, String backCallback) {
        InlineKeyboardRow row1 = new InlineKeyboardRow();
        row1.add(playButton("Starting...", playlistId));

        InlineKeyboardRow row2 = new InlineKeyboardRow();
        row2.add(backButton(backCallback));

        return InlineKeyboardMarkup.builder()
                .keyboardRow(row1)
                .keyboardRow(row2)
                .build();
    }

    public InlineKeyboardMarkup success(String playlistId, String backCallback) {
        InlineKeyboardRow row1 = new InlineKeyboardRow();
        row1.add(openButton(playlistId));

        InlineKeyboardRow row2 = new InlineKeyboardRow();
        row2.add(playButton("Play Again", playlistId));
        row2.add(shuffleButton());

        InlineKeyboardRow row3 = new InlineKeyboardRow();
        row3.add(backButton(backCallback));

        return InlineKeyboardMarkup.builder()
                .keyboardRow(row1)
                .keyboardRow(row2)
                .keyboardRow(row3)
                .build();
    }

    public InlineKeyboardMarkup failure(String playlistId, String backCallback) {
        InlineKeyboardRow row1 = new InlineKeyboardRow();
        row1.add(playButton("Try Again", playlistId));

        InlineKeyboardRow row2 = new InlineKeyboardRow();
        row2.add(openButton(playlistId));
        row2.add(shuffleButton());

        InlineKeyboardRow row3 = new InlineKeyboardRow();
        row3.add(backButton(backCallback));

        return InlineKeyboardMarkup.builder()
                .keyboardRow(row1)
                .keyboardRow(row2)
                .keyboardRow(row3)
                .build();
    }

    public InlineKeyboardMarkup authRequired(String loginUrl, String playlistId, String backCallback) {
        InlineKeyboardRow row1 = new InlineKeyboardRow();
        row1.add(InlineKeyboardButton.builder()
                .text("Connect Spotify")
                .url(loginUrl)
                .build());

        InlineKeyboardRow row2 = new InlineKeyboardRow();
        row2.add(openButton(playlistId));

        InlineKeyboardRow row3 = new InlineKeyboardRow();
        row3.add(backButton(backCallback));

        return InlineKeyboardMarkup.builder()
                .keyboardRow(row1)
                .keyboardRow(row2)
                .keyboardRow(row3)
                .build();
    }

    private InlineKeyboardButton openButton(String playlistId) {
        return InlineKeyboardButton.builder()
                .text("Open in Spotify")
                .url(PLAYLIST_URL_PREFIX + playlistId)
                .build();
    }

    private InlineKeyboardButton playButton(String text, String playlistId) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData("play_playlist:" + playlistId)
                .build();
    }

    private InlineKeyboardButton shuffleButton() {
        return InlineKeyboardButton.builder()
                .text("Shuffle Again")
                .callbackData("shuffle")
                .build();
    }

    private InlineKeyboardButton backButton(String callback) {
        return InlineKeyboardButton.builder()
                .text("Back")
                .callbackData(callback)
                .build();
    }
}
