package org.example.deepshuffle.factory;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Component
public class MainMenuKeyboardFactory {

    public ReplyKeyboardMarkup create(){

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Shuffle");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Random Genre");

        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row1, row2))
                .resizeKeyboard(true)
                .build();

    }

}
