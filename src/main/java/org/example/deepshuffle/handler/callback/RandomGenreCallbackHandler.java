package org.example.deepshuffle.handler.callback;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.factory.GenreKeyboardFactory;
import org.example.deepshuffle.service.TelegramMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class RandomGenreCallbackHandler implements CallbackHandler{

    private final TelegramMessageService messageService;

    private final GenreKeyboardFactory genreKeyboardFactory;


    @Override
    public boolean supports(String callback) {
        return callback.startsWith("random_genre");
    }

    @Override
    public void handle(Update update) {

        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        messageService.sendMessage(chatId, "Choose genre", genreKeyboardFactory.create());

    }
}
