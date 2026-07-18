package com.example.urlshortener.service;

import com.example.urlshortener.dto.response.ShortenUrlResponse;

public record ShortenUrlResult(boolean created, ShortenUrlResponse response) {
}
