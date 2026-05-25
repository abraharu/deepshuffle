package org.example.deepshuffle.bot.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class MainMenuKeyboardFactory {

    public InlineKeyboardMarkup create(){

        InlineKeyboardButton shuffleButon = InlineKeyboardButton.builder()
                .text("Shuffle")
                .callbackData("shuffle")
                .build();


        InlineKeyboardButton genreButton = InlineKeyboardButton.builder()
                .text("Random Genre")
                .callbackData("random_genre")
                .build();

        InlineKeyboardButton tasteButton = InlineKeyboardButton.builder()
                .text("Sync Taste")
                .callbackData("sync_taste")
                .build();

        InlineKeyboardButton reconnectButton = InlineKeyboardButton.builder()
                .text("Reconnect Spotify")
                .callbackData("reconnect_spotify")
                .build();

       InlineKeyboardRow row1 = new InlineKeyboardRow();
       row1.add(shuffleButon);

        InlineKeyboardRow row2 = new InlineKeyboardRow();
        row2.add(genreButton);

        InlineKeyboardRow row3 = new InlineKeyboardRow();
        row3.add(tasteButton);

        InlineKeyboardRow row4 = new InlineKeyboardRow();
        row4.add(reconnectButton);

        return InlineKeyboardMarkup.builder()
                .keyboardRow(row1)
                .keyboardRow(row2)
                .keyboardRow(row3)
                .keyboardRow(row4)
                .build();

    }

}
