package org.example.deepshuffle.bot.callback;


import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.bot.keyboard.MainMenuKeyboardFactory;
import org.example.deepshuffle.bot.keyboard.TasteKeyboardFactory;
import org.example.deepshuffle.bot.taste.TasteMessageFormatter;
import org.example.deepshuffle.service.TelegramMessageService;
import org.example.deepshuffle.spotify.taste.service.UserTasteProfileService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class BackToMenuCallbackHandler implements CallbackHandler{

    private final MainMenuKeyboardFactory mainMenuKeyboardFactory;
    private final TasteKeyboardFactory tasteKeyboardFactory;
    private final TasteMessageFormatter tasteMessageFormatter;
    private final UserTasteProfileService tasteProfileService;

    private final TelegramMessageService messageService;

    @Override
    public boolean supports(String callback) {
        return callback.equals("back_to_menu") || callback.startsWith("back:");
    }

    @Override
    public void handle(Update update) {

        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long telegramUserId = update.getCallbackQuery().getFrom().getId();
        String callback = update.getCallbackQuery().getData();

        messageService.answerCallback(update.getCallbackQuery().getId(), "Back");
        if (callback.equals("back:taste_settings")) {
            int currentLevel = tasteProfileService.getRandomnessLevel(telegramUserId);
            messageService.sendMessage(
                    chatId,
                    tasteMessageFormatter.randomnessSettings(currentLevel),
                    tasteKeyboardFactory.settings()
            );
            return;
        }

        messageService.sendMessage(chatId, "Main menu", mainMenuKeyboardFactory.create());
    }
}
