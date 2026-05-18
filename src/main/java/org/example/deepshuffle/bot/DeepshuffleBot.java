package org.example.deepshuffle.bot;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.handler.CommandRouter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DeepshuffleBot implements LongPollingUpdateConsumer {

    private final CommandRouter commandRouter;

    @Override
    public void consume(List<Update> updates) {
        for (Update update : updates) {
          commandRouter.route(update);
        }
    }
}
