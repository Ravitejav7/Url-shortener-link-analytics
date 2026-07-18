package com.example.urlshortener.service;

import com.example.urlshortener.dto.response.AnalyticsResponse;

public interface AnalyticsService {

    void recordClick(String code, String userAgent, String referrer);

    AnalyticsResponse getAnalytics(String code);
}
