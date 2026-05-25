package org.example.deepshuffle.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "deepshuffle.deployment")
public record DeploymentProperties(String environmentName,
                                   boolean stageMode) {
}
