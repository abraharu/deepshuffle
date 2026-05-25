package org.example.deepshuffle.bot.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class SpotifyAuthKeyboardFactory {

    public InlineKeyboardMarkup reconnect(String loginUrl) {
        InlineKeyboardRow row1 = new InlineKeyboardRow();
        row1.add(InlineKeyboardButton.builder()
                .text("Open Spotify Login")
                .url(loginUrl)
                .build());

        InlineKeyboardRow row2 = new InlineKeyboardRow();
        row2.add(InlineKeyboardButton.builder()
                .text("Sync Taste")
                .callbackData("sync_taste")
                .build());

        return InlineKeyboardMarkup.builder()
                .keyboardRow(row1)
                .keyboardRow(row2)
                .build();
    }

    public InlineKeyboardMarkup reconnectAction() {
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(InlineKeyboardButton.builder()
                .text("Reconnect Spotify")
                .callbackData("reconnect_spotify")
                .build());

        return InlineKeyboardMarkup.builder()
                .keyboardRow(row)
                .build();
    }
}
