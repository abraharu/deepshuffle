package org.example.deepshuffle.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class DeepshuffleBot implements LongPollingUpdateConsumer {

    private final OkHttpTelegramClient client;

    public DeepshuffleBot(@Value("${telegram.bot.token}") String token) {
        this.client = new OkHttpTelegramClient(token);
    }

    @Override
    public void consume(List<Update> updates) {
        for (Update update : updates) {
            if (!update.hasMessage()){
                continue;
            }

            String text = update.getMessage().getText();

            SendMessage message = SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text("You said:" + text)
                    .build();
            try {
                client.execute(message);
            } catch (TelegramApiException e){
                throw new RuntimeException(e);
            }
        }
    }
}
