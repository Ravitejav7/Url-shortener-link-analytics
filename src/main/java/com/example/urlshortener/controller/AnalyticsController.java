package com.example.urlshortener.controller;

import com.example.urlshortener.dto.response.AnalyticsResponse;
import com.example.urlshortener.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/analytics/{code}")
    public AnalyticsResponse getAnalytics(@PathVariable String code) {
        return analyticsService.getAnalytics(code);
    }
}
