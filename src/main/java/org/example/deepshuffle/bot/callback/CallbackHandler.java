package org.example.deepshuffle.bot.callback;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackHandler {

    boolean supports(String callback);

    void handle(Update update);
}
