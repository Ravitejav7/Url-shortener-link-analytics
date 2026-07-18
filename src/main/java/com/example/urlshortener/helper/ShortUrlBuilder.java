package com.example.urlshortener.helper;

import com.example.urlshortener.config.AppProperties;
import org.springframework.stereotype.Component;

@Component
public class ShortUrlBuilder {

    private final AppProperties appProperties;

    public ShortUrlBuilder(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public String build(String code) {
        String baseUrl = appProperties.baseUrl();
        if (baseUrl.endsWith("/")) {
            return baseUrl + code;
        }
        return baseUrl + "/" + code;
    }
}
