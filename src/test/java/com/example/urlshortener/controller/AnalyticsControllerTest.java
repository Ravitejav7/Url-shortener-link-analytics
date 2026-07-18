package com.example.urlshortener.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import com.example.urlshortener.dto.response.AnalyticsResponse;
import com.example.urlshortener.dto.response.ClickEventResponse;
import com.example.urlshortener.exception.GlobalExceptionHandler;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AnalyticsControllerTest {

    private AnalyticsService analyticsService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        analyticsService = org.mockito.Mockito.mock(AnalyticsService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AnalyticsController(analyticsService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void analyticsReturnsClickCountAndRecentClicks() throws Exception {
        when(analyticsService.getAnalytics("abc123XY")).thenReturn(new AnalyticsResponse(
                "abc123XY",
                "https://example.com",
                1,
                Instant.parse("2026-07-19T00:00:00Z"),
                List.of(new ClickEventResponse(Instant.parse("2026-07-19T00:01:00Z"), "JUnit", null))
        ));

        mockMvc.perform(get("/analytics/abc123XY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("abc123XY"))
                .andExpect(jsonPath("$.clickCount").value(1))
                .andExpect(jsonPath("$.recentClicks[0].userAgent").value("JUnit"));
    }

    @Test
    void analyticsUnknownCodeReturnsNotFound() throws Exception {
        when(analyticsService.getAnalytics("missing")).thenThrow(new UrlNotFoundException("missing"));

        mockMvc.perform(get("/analytics/missing"))
                .andExpect(status().isNotFound());
    }
}
