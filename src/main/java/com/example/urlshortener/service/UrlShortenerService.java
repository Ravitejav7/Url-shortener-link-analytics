package com.example.urlshortener.service;

import com.example.urlshortener.dto.request.ShortenUrlRequest;

public interface UrlShortenerService {

    ShortenUrlResult shorten(ShortenUrlRequest request);

    String getOriginalUrl(String code);
}
