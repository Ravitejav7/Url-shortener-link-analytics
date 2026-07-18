package com.example.urlshortener.controller;

import com.example.urlshortener.dto.request.ShortenUrlRequest;
import com.example.urlshortener.dto.response.ShortenUrlResponse;
import com.example.urlshortener.service.ShortenUrlResult;
import com.example.urlshortener.service.UrlShortenerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UrlController {

    private final UrlShortenerService urlShortenerService;

    public UrlController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponse> shorten(@RequestBody(required = false) ShortenUrlRequest request) {
        ShortenUrlResult result = urlShortenerService.shorten(request);
        HttpStatus status = result.created() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(result.response());
    }
}
