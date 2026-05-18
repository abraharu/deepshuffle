package org.example.deepshuffle.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

public class StartCommandHandler implements CommandHandler{


    @Override
    public boolean handle(String command) {
        return command.equals("/start");
    }

    @Override
    public void handle(Update update) {
        System.out.println("start command");
    }
}
