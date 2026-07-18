package com.example.urlshortener.dto.response;

public record ShortenUrlResponse(
        String code,
        String shortUrl,
        String originalUrl
) {
}
