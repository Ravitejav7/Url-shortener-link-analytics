package com.example.urlshortener.controller;

import java.net.URI;

import com.example.urlshortener.dto.request.ShortenUrlRequest;
import com.example.urlshortener.dto.response.ShortenUrlResponse;
import com.example.urlshortener.service.AnalyticsService;
import com.example.urlshortener.service.ShortenUrlResult;
import com.example.urlshortener.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UrlController {

    private final UrlShortenerService urlShortenerService;
    private final AnalyticsService analyticsService;

    public UrlController(UrlShortenerService urlShortenerService, AnalyticsService analyticsService) {
        this.urlShortenerService = urlShortenerService;
        this.analyticsService = analyticsService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponse> shorten(@RequestBody(required = false) ShortenUrlRequest request) {
        ShortenUrlResult result = urlShortenerService.shorten(request);
        HttpStatus status = result.created() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(result.response());
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code, HttpServletRequest request) {
        String originalUrl = urlShortenerService.getOriginalUrl(code);
        analyticsService.recordClick(code, request.getHeader("User-Agent"), request.getHeader("Referer"));
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .location(URI.create(originalUrl))
                .build();
    }
}
