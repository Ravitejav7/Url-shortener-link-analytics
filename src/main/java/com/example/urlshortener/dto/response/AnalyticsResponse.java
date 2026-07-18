package com.example.urlshortener.dto.response;

import java.time.Instant;
import java.util.List;

public record AnalyticsResponse(
        String code,
        String originalUrl,
        long clickCount,
        Instant createdAt,
        List<ClickEventResponse> recentClicks
) {
}
