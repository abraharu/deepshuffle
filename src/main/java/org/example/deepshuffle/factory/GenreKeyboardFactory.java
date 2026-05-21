package org.example.deepshuffle.factory;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class GenreKeyboardFactory {

    public InlineKeyboardMarkup create(){

        InlineKeyboardButton phonk = InlineKeyboardButton.builder()
                .text("Phonk")
                .callbackData("genre_phonk")
                .build();

        InlineKeyboardButton rock = InlineKeyboardButton.builder()
                .text("Rock")
                .callbackData("genre_rock")
                .build();

        InlineKeyboardButton back = InlineKeyboardButton.builder()
                .text("⬅ Back")
                .callbackData("back_to_menu")
                .build();

        InlineKeyboardRow row1 = new InlineKeyboardRow();
        row1.add(phonk);

        InlineKeyboardRow row2 = new InlineKeyboardRow();

        row2.add(rock);

        InlineKeyboardRow row3 = new InlineKeyboardRow();

        row3.add(back);

        return InlineKeyboardMarkup.builder()
                .keyboardRow(row1)
                .keyboardRow(row2)
                .keyboardRow(row3)
                .build();

    }
}
