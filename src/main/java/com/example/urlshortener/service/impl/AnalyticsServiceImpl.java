package com.example.urlshortener.service.impl;

import java.util.List;

import com.example.urlshortener.dto.response.AnalyticsResponse;
import com.example.urlshortener.dto.response.ClickEventResponse;
import com.example.urlshortener.entity.ClickEvent;
import com.example.urlshortener.entity.UrlMapping;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.repository.ClickEventRepository;
import com.example.urlshortener.repository.UrlMappingRepository;
import com.example.urlshortener.service.AnalyticsService;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UrlMappingRepository urlMappingRepository;
    private final ClickEventRepository clickEventRepository;

    public AnalyticsServiceImpl(
            UrlMappingRepository urlMappingRepository,
            ClickEventRepository clickEventRepository
    ) {
        this.urlMappingRepository = urlMappingRepository;
        this.clickEventRepository = clickEventRepository;
    }

    @Override
    public void recordClick(String code, String userAgent, String referrer) {
        clickEventRepository.save(new ClickEvent(code, sanitizeHeader(userAgent), sanitizeHeader(referrer)));
    }

    @Override
    public AnalyticsResponse getAnalytics(String code) {
        UrlMapping mapping = urlMappingRepository.findByCode(code)
                .orElseThrow(() -> new UrlNotFoundException(code));

        long clickCount = clickEventRepository.countByCode(code);
        List<ClickEventResponse> recentClicks = clickEventRepository.findTop20ByCodeOrderByClickedAtDesc(code)
                .stream()
                .map(event -> new ClickEventResponse(event.getClickedAt(), event.getUserAgent(), event.getReferrer()))
                .toList();

        return new AnalyticsResponse(
                mapping.getCode(),
                mapping.getOriginalUrl(),
                clickCount,
                mapping.getCreatedAt(),
                recentClicks
        );
    }

    private String sanitizeHeader(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmedValue = value.trim();
        return trimmedValue.length() > 500 ? trimmedValue.substring(0, 500) : trimmedValue;
    }
}
