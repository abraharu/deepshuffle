package org.example.deepshuffle.bot;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.deepshuffle.config.TelegramBotProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

@Component
@Slf4j
@RequiredArgsConstructor
public class BotInitializer {

    private final DeepshuffleBot bot;
    private final TelegramBotProperties telegramBotProperties;

    private TelegramBotsLongPollingApplication app;

    @PostConstruct
    public void init() {
        String token = telegramBotProperties.token();
        if (!StringUtils.hasText(token)) {
            log.warn("Telegram bot token is not configured. Bot registration is skipped.");
            return;
        }

        try {
            this.app = new TelegramBotsLongPollingApplication();
            app.registerBot(token, bot);
            log.info("Telegram long polling bot registered successfully");
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
