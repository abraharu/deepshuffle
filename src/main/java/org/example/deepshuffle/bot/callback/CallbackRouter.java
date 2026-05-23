package org.example.deepshuffle.bot.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CallbackRouter {

    private final List<CallbackHandler> callbackHandlers;

    public void route(Update update){

        String callback = update.getCallbackQuery().getData();

        for (CallbackHandler handler : callbackHandlers){
            if (handler.supports(callback)) {
                handler.handle(update);

                return;
            }
        }
    }
}
