package com.example.urlshortener.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String baseUrl,
        Analytics analytics
) {

    public int recentClickLimit() {
        if (analytics == null || analytics.recentClickLimit() == null || analytics.recentClickLimit() < 1) {
            return 20;
        }
        return analytics.recentClickLimit();
    }

    public record Analytics(Integer recentClickLimit) {
    }
}
