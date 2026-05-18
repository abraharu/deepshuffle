package org.example.deepshuffle.bot;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

@Component
@RequiredArgsConstructor
public class BotInitializer {

    @Value("${telegram.bot.token}")
    private String token;

    private final DeepshuffleBot bot;

    private TelegramBotsLongPollingApplication app;

    @PostConstruct
    public void init() {
        try {
            this.app = new TelegramBotsLongPollingApplication();
            app.registerBot(token, bot);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void destroy() throws Exception {
        if (app != null) {
            app.close();
        }
    }

}
