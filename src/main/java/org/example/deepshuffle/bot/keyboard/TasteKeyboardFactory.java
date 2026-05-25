package org.example.deepshuffle.bot.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class TasteKeyboardFactory {

    public InlineKeyboardMarkup settings() {
        InlineKeyboardRow row1 = new InlineKeyboardRow();
        row1.add(randomnessButton("Safe", 15));
        row1.add(randomnessButton("Balanced", 50));

        InlineKeyboardRow row2 = new InlineKeyboardRow();
        row2.add(randomnessButton("Deep", 75));
        row2.add(randomnessButton("Chaos", 95));

        InlineKeyboardRow row3 = new InlineKeyboardRow();
        row3.add(InlineKeyboardButton.builder()
                .text("🧬 Sync Taste")
                .callbackData("sync_taste")
                .build());

        InlineKeyboardRow row4 = new InlineKeyboardRow();
        row4.add(backButton("back:main"));

        return InlineKeyboardMarkup.builder()
                .keyboardRow(row1)
                .keyboardRow(row2)
                .keyboardRow(row3)
                .keyboardRow(row4)
                .build();
    }

    public InlineKeyboardMarkup afterUpdate() {
        InlineKeyboardRow row1 = new InlineKeyboardRow();
        row1.add(InlineKeyboardButton.builder()
                .text("🎲 Shuffle")
                .callbackData("shuffle")
                .build());
        row1.add(InlineKeyboardButton.builder()
                .text("🎚 Change Randomness")
                .callbackData("taste_settings")
                .build());

        InlineKeyboardRow row2 = new InlineKeyboardRow();
        row2.add(InlineKeyboardButton.builder()
                .text("🧬 Sync Taste")
                .callbackData("sync_taste")
                .build());

        InlineKeyboardRow row3 = new InlineKeyboardRow();
        row3.add(backButton("back:taste_settings"));

        return InlineKeyboardMarkup.builder()
                .keyboardRow(row1)
                .keyboardRow(row2)
                .keyboardRow(row3)
                .build();
    }

    public InlineKeyboardMarkup syncResult() {
        InlineKeyboardRow row1 = new InlineKeyboardRow();
        row1.add(InlineKeyboardButton.builder()
                .text("🎚 Set Randomness")
                .callbackData("taste_settings")
                .build());
        row1.add(InlineKeyboardButton.builder()
                .text("🎲 Shuffle")
                .callbackData("shuffle")
                .build());

        InlineKeyboardRow row2 = new InlineKeyboardRow();
        row2.add(backButton("back:main"));

        return InlineKeyboardMarkup.builder()
                .keyboardRow(row1)
                .keyboardRow(row2)
                .build();
    }

    private InlineKeyboardButton randomnessButton(String label, int level) {
        return InlineKeyboardButton.builder()
                .text("%s %d%%".formatted(label, level))
                .callbackData("set_randomness:" + level)
                .build();
    }

    private InlineKeyboardButton backButton(String callback) {
        return InlineKeyboardButton.builder()
                .text("⬅️ Back")
                .callbackData(callback)
                .build();
    }
}
