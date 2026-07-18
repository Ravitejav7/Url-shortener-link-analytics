package com.example.urlshortener.dto.response;

import java.time.Instant;

public record ClickEventResponse(
        Instant clickedAt,
        String userAgent,
        String referrer
) {
}
