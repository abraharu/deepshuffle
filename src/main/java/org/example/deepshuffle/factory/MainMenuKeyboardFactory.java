package org.example.deepshuffle.factory;

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

       InlineKeyboardRow row1 = new InlineKeyboardRow();
       row1.add(shuffleButon);

        InlineKeyboardRow row2 = new InlineKeyboardRow();
        row2.add(genreButton);

        return InlineKeyboardMarkup.builder()
                .keyboardRow(row1)
                .keyboardRow(row2)
                .build();

    }

}
