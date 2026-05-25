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
public class SetRandomnessCallbackHandler implements CallbackHandler {

    private static final String CALLBACK_PREFIX = "set_randomness:";

    private final UserTasteProfileService tasteProfileService;
    private final TelegramMessageService messageService;
    private final TasteMessageFormatter messageFormatter;
    private final TasteKeyboardFactory keyboardFactory;

    @Override
    public boolean supports(String callback) {
        return callback.startsWith(CALLBACK_PREFIX);
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long telegramUserId = update.getCallbackQuery().getFrom().getId();
        int requestedLevel = Integer.parseInt(update.getCallbackQuery().getData().substring(CALLBACK_PREFIX.length()));
        int savedLevel = tasteProfileService.updateRandomnessLevel(telegramUserId, requestedLevel);

        messageService.answerCallback(update.getCallbackQuery().getId(), "Randomness updated");
        messageService.sendMessage(
                chatId,
                messageFormatter.randomnessUpdated(savedLevel),
                keyboardFactory.afterUpdate()
        );
    }
}
