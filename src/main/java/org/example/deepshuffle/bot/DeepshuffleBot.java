package org.example.deepshuffle.bot;

import lombok.RequiredArgsConstructor;
import okhttp3.Call;
import okhttp3.Callback;
import org.example.deepshuffle.handler.callback.CallbackRouter;
import org.example.deepshuffle.handler.command.CommandRouter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DeepshuffleBot implements LongPollingUpdateConsumer {

    private final CommandRouter commandRouter;

    private final CallbackRouter callbackRouter;

    @Override
    public void consume(List<Update> updates) {
        for (Update update : updates) {

            if(update.hasCallbackQuery()){
                callbackRouter.route(update);

                continue;
            }

            if (update.hasMessage()){
                commandRouter.route(update);
            }
        }
    }
}
