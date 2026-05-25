package org.example.deepshuffle.bot;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.bot.callback.CallbackRouter;
import org.example.deepshuffle.bot.command.CommandRouter;
import org.example.deepshuffle.bot.message.PlaylistVibeMessageHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DeepshuffleBot implements LongPollingUpdateConsumer {

    private final CommandRouter commandRouter;

    private final CallbackRouter callbackRouter;

    private final PlaylistVibeMessageHandler playlistVibeMessageHandler;

    @Override
    public void consume(List<Update> updates) {
        for (Update update : updates) {

            if(update.hasCallbackQuery()){
                callbackRouter.route(update);

                continue;
            }

            if (playlistVibeMessageHandler.supports(update)) {
                playlistVibeMessageHandler.handle(update);
                continue;
            }

            if (update.hasMessage()){
                commandRouter.route(update);
            }
        }
    }
}
