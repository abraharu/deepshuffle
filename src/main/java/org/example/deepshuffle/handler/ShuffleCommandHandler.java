package org.example.deepshuffle.handler;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.model.CommandContext;
import org.example.deepshuffle.service.ShuffleDiscoveryService;
import org.example.deepshuffle.service.TelegramMessageService;
import org.example.deepshuffle.spotify.dto.Playlist;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ShuffleCommandHandler implements CommandHandler{

    private final TelegramMessageService messageService;

    private final ShuffleDiscoveryService shuffleDiscoveryService;

    @Override
    public boolean supports(String command) {
        return command.equals("/shuffle");
    }

    @Override
    public void handle(Update update, CommandContext context) {

        Playlist playlist = shuffleDiscoveryService.discoverRandomPlaylist();

        if(playlist == null){
            messageService.sendMessage(update.getMessage().getChatId(), "No playlists found");
            return;
        }

        String response = """
                🎧 %s
                
                👤 %s
                
                %s
                """.formatted(playlist.name(), playlist.owner(), playlist.url());

        InlineKeyboardButton openButton = InlineKeyboardButton.builder()
                  .text("OpenSpotify")
                  .url(playlist.url())
                  .build();

        InlineKeyboardRow row =
                new InlineKeyboardRow();

        row.add(openButton);

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                        .keyboardRow(row)
                        .build();


        messageService.sendMessage(update.getMessage().getChatId(), response, markup);
    }
}
