package org.example.deepshuffle.bot.callback;


import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.bot.keyboard.MainMenuKeyboardFactory;
import org.example.deepshuffle.service.TelegramMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class BackToMenuCallbackHandler implements CallbackHandler{

    private final MainMenuKeyboardFactory mainMenuKeyboardFactory;

    private final TelegramMessageService messageService;

    @Override
    public boolean supports(String callback) {
        return callback.equals("back_to_menu");
    }

    @Override
    public void handle(Update update) {

        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        messageService.sendMessage(chatId, "Main menu", mainMenuKeyboardFactory.create());
    }
}
