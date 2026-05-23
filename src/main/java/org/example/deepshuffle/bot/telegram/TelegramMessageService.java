package org.example.deepshuffle.bot.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class TelegramMessageService {
    private final OkHttpTelegramClient client;

    public TelegramMessageService(@Value("${telegram.bot.token}") String token) {
        this.client = new OkHttpTelegramClient(token);
    }

    public void sendMessage(Long chatId, String text){
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();

        try {
            client.execute(message);
        } catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(Long chatId, String text, InlineKeyboardMarkup markup){
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .build();

        try {
            client.execute(message);
        } catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(Long chatId, String text, ReplyKeyboardMarkup markup){
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .build();

        try {
            client.execute(message);
        } catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }
}
