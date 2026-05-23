package org.example.deepshuffle.bot.callback;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.bot.keyboard.GenreKeyboardFactory;
import org.example.deepshuffle.bot.telegram.TelegramMessageService;
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
