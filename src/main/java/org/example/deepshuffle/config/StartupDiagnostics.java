package org.example.deepshuffle.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.Arrays;

@Component
@Slf4j
@RequiredArgsConstructor
public class StartupDiagnostics implements ApplicationRunner {

    private final Environment environment;
    private final DeploymentProperties deploymentProperties;
    private final TelegramBotProperties telegramBotProperties;
    private final SpotifyProperties spotifyProperties;
    private final DiscoveryCrawlerProperties crawlerProperties;

    @Override
    public void run(ApplicationArguments args) {
        log.info(
                "DeepShuffle startup: environment={}, profiles={}, stageMode={}, crawlerEnabled={}, crawlerDelayMs={}",
                deploymentProperties.environmentName(),
                Arrays.toString(environment.getActiveProfiles()),
                deploymentProperties.stageMode(),
                crawlerProperties.enabled(),
                crawlerProperties.fixedDelayMs()
        );
        log.info(
                "Runtime diagnostics: telegramBotConfigured={}, spotifyRedirectHost={}, datasourceHost={}",
                StringUtils.hasText(telegramBotProperties.token()),
                hostOf(spotifyProperties.redirectUri()),
                datasourceHost()
        );
    }

    private String datasourceHost() {
        String datasourceUrl = environment.getProperty("spring.datasource.url");
        if (!StringUtils.hasText(datasourceUrl)) {
            return "not-configured";
        }
        if (datasourceUrl.startsWith("jdbc:postgresql://")) {
            String sanitized = datasourceUrl.replace("jdbc:postgresql://", "");
            int slashIndex = sanitized.indexOf('/');
            return slashIndex > 0 ? sanitized.substring(0, slashIndex) : sanitized;
        }
        return "custom";
    }

    private String hostOf(String value) {
        if (!StringUtils.hasText(value)) {
            return "not-configured";
        }
        try {
            return URI.create(value).getHost();
        } catch (Exception e) {
            return "invalid";
        }
    }
}
