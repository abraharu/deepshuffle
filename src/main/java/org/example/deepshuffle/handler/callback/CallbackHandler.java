package org.example.deepshuffle.handler.callback;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackHandler {

    boolean supports(String callback);

    void handle(Update update);
}
