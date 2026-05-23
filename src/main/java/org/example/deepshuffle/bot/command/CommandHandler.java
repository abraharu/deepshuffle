package org.example.deepshuffle.bot.command;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {

    boolean supports(String command);

    void handle(Update update, CommandContext context);
}
