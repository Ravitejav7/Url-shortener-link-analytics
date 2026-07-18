package com.example.urlshortener.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.urlshortener.dto.response.ShortenUrlResponse;
import com.example.urlshortener.exception.BadRequestException;
import com.example.urlshortener.exception.GlobalExceptionHandler;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.service.AnalyticsService;
import com.example.urlshortener.service.ShortenUrlResult;
import com.example.urlshortener.service.UrlShortenerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class UrlControllerTest {

    private UrlShortenerService urlShortenerService;
    private AnalyticsService analyticsService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        urlShortenerService = org.mockito.Mockito.mock(UrlShortenerService.class);
        analyticsService = org.mockito.Mockito.mock(AnalyticsService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UrlController(urlShortenerService, analyticsService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void rootReturnsApiStatus() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("URL Shortener & Link Analytics"))
                .andExpect(jsonPath("$.status").value("running"))
                .andExpect(jsonPath("$.endpoints.shorten").value("POST /shorten"));
    }

    @Test
    void shortenReturnsCreatedForNewMapping() throws Exception {
        when(urlShortenerService.shorten(org.mockito.ArgumentMatchers.any()))
                .thenReturn(new ShortenUrlResult(true,
                        new ShortenUrlResponse("abc123XY", "http://localhost:8080/abc123XY", "https://example.com")));

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("abc123XY"))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/abc123XY"));
    }

    @Test
    void shortenReturnsBadRequestForInvalidUrl() throws Exception {
        when(urlShortenerService.shorten(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new BadRequestException("Only http and https URLs are supported"));

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"javascript:alert(1)\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Only http and https URLs are supported"));
    }

    @Test
    void malformedJsonReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{url:\"https://example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request body must be valid JSON"));
    }

    @Test
    void redirectReturnsMovedPermanentlyAndRecordsAnalytics() throws Exception {
        when(urlShortenerService.getOriginalUrl("abc123XY")).thenReturn("https://example.com");

        mockMvc.perform(get("/abc123XY")
                        .header("User-Agent", "JUnit")
                        .header("Referer", "https://referrer.test"))
                .andExpect(status().isMovedPermanently())
                .andExpect(header().string("Location", "https://example.com"));

        verify(analyticsService).recordClick("abc123XY", "JUnit", "https://referrer.test");
    }

    @Test
    void unknownCodeReturnsNotFound() throws Exception {
        when(urlShortenerService.getOriginalUrl("missing")).thenThrow(new UrlNotFoundException("missing"));

        mockMvc.perform(get("/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("missing")));
    }
}
