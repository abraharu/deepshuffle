package org.example.deepshuffle.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {

    boolean handle(String command);

    void handle(Update update);
}
