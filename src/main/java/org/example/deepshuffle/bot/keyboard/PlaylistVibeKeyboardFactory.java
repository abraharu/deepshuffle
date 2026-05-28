package org.example.deepshuffle.bot.keyboard;

import org.example.deepshuffle.spotify.dto.Playlist;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@Component
public class PlaylistVibeKeyboardFactory {

    private static final String VIEW_PLAYLIST_PREFIX = "view_vibe_playlist:";
    private static final String BACK_TO_LIST_CALLBACK = "back:vibe_list";
    private static final String BACK_TO_MENU_CALLBACK = "back:main";

    public InlineKeyboardMarkup list(List<Playlist> playlists) {
        InlineKeyboardMarkup.InlineKeyboardMarkupBuilder builder = InlineKeyboardMarkup.builder();

        for (int i = 0; i < playlists.size(); i++) {
            Playlist playlist = playlists.get(i);
            InlineKeyboardRow row = new InlineKeyboardRow();
            row.add(InlineKeyboardButton.builder()
                    .text("%d. %s".formatted(i + 1, shorten(playlist.name())))
                    .callbackData(VIEW_PLAYLIST_PREFIX + playlist.id())
                    .build());
            builder.keyboardRow(row);
        }

        InlineKeyboardRow backRow = new InlineKeyboardRow();
        backRow.add(InlineKeyboardButton.builder()
                .text("Back")
                .callbackData(BACK_TO_MENU_CALLBACK)
                .build());

        return builder.keyboardRow(backRow).build();
    }

    public InlineKeyboardMarkup detail(Playlist playlist) {
        InlineKeyboardRow row1 = new InlineKeyboardRow();
        row1.add(InlineKeyboardButton.builder()
                .text("Open")
                .url(playlist.url())
                .build());
        row1.add(InlineKeyboardButton.builder()
                .text("Play")
                .callbackData("play_playlist:" + playlist.id())
                .build());

        InlineKeyboardRow row2 = new InlineKeyboardRow();
        row2.add(InlineKeyboardButton.builder()
                .text("Back to list")
                .callbackData(BACK_TO_LIST_CALLBACK)
                .build());

        return InlineKeyboardMarkup.builder()
                .keyboardRow(row1)
                .keyboardRow(row2)
                .build();
    }

    private String shorten(String value) {
        if (value == null || value.isBlank()) {
            return "Unknown playlist";
        }
        if (value.length() <= 40) {
            return value;
        }
        return value.substring(0, 37) + "...";
    }
}
