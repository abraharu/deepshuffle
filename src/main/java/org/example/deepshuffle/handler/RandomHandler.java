package org.example.deepshuffle.handler;

import org.example.deepshuffle.model.CommandContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class RandomHandler implements CommandHandler{
    @Override
    public boolean supports(String command) {
        return command.equals("/random");
    }

    @Override
    public void handle(Update update, CommandContext context) {

    }
}
