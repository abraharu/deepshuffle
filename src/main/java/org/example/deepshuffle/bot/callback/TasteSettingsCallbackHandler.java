package org.example.deepshuffle.bot.callback;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.bot.keyboard.TasteKeyboardFactory;
import org.example.deepshuffle.bot.taste.TasteMessageFormatter;
import org.example.deepshuffle.service.TelegramMessageService;
import org.example.deepshuffle.spotify.taste.service.UserTasteProfileService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class TasteSettingsCallbackHandler implements CallbackHandler {

    private static final String CALLBACK = "taste_settings";

    private final UserTasteProfileService tasteProfileService;
    private final TelegramMessageService messageService;
    private final TasteMessageFormatter messageFormatter;
    private final TasteKeyboardFactory keyboardFactory;

    @Override
    public boolean supports(String callback) {
        return CALLBACK.equals(callback);
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long telegramUserId = update.getCallbackQuery().getFrom().getId();
        int currentLevel = tasteProfileService.getRandomnessLevel(telegramUserId);

        messageService.answerCallback(update.getCallbackQuery().getId(), "Randomness settings");
        messageService.sendMessage(
                chatId,
                messageFormatter.randomnessSettings(currentLevel),
                keyboardFactory.settings()
        );
    }
}
