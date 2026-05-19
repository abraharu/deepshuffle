package org.example.deepshuffle.handler;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.model.CommandContext;
import org.example.deepshuffle.service.PlaylistService;
import org.example.deepshuffle.service.TelegramMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class RandomCommandHandler implements CommandHandler{

    private final PlaylistService playlistService;

    private final TelegramMessageService messageService;


    @Override
    public boolean supports(String command) {
        return command.equals("/random");
    }

    @Override
    public void handle(Update update, CommandContext context) {

        if(context.arguments().isEmpty()){
            messageService.sendMessage(update.getMessage().getChatId(), "Используйте: /random <genre>");

            return;
        }

        String genre = context.arguments().getFirst();

        String playlist = playlistService.getRandomPlaylist(genre);

        if(playlist == null){
            messageService.sendMessage(update.getMessage().getChatId(), "No playlists found for genre: " + genre);

            return;
        }

        messageService.sendMessage(update.getMessage().getChatId(), playlist);

    }
}
