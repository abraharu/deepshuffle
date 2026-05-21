package org.example.deepshuffle.handler;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.factory.MainMenuKeyboardFactory;
import org.example.deepshuffle.model.CommandContext;
import org.example.deepshuffle.service.ShuffleDiscoveryService;
import org.example.deepshuffle.service.TelegramMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler{

    private final TelegramMessageService messageService;

    private final MainMenuKeyboardFactory mainMenuKeyboardFactory;

    @Override
    public boolean supports(String command) {
        return command.equals("/start");
    }

    @Override
    public void handle(Update update, CommandContext context) {
        messageService.sendMessage(update.getMessage().getChatId(), "Welcome to DeepShuffle",  mainMenuKeyboardFactory.create());
    }
}
