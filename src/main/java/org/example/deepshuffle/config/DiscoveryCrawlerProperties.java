package org.example.deepshuffle.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "deepshuffle.discovery.crawler")
public record DiscoveryCrawlerProperties(boolean enabled,
                                         long fixedDelayMs,
                                         long initialDelayMs) {
}
