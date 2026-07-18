package com.example.urlshortener.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.example.urlshortener.dto.response.AnalyticsResponse;
import com.example.urlshortener.entity.ClickEvent;
import com.example.urlshortener.entity.UrlMapping;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.repository.ClickEventRepository;
import com.example.urlshortener.repository.UrlMappingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock
    private UrlMappingRepository urlMappingRepository;

    @Mock
    private ClickEventRepository clickEventRepository;

    @InjectMocks
    private AnalyticsServiceImpl service;

    @Test
    void recordsClickWithoutIpAddress() {
        service.recordClick("abc123XY", "Mozilla", "https://referrer.test");

        ArgumentCaptor<ClickEvent> captor = ArgumentCaptor.forClass(ClickEvent.class);
        verify(clickEventRepository).save(captor.capture());

        assertThat(captor.getValue().getCode()).isEqualTo("abc123XY");
        assertThat(captor.getValue().getUserAgent()).isEqualTo("Mozilla");
        assertThat(captor.getValue().getReferrer()).isEqualTo("https://referrer.test");
    }

    @Test
    void returnsAnalyticsForKnownCode() {
        UrlMapping mapping = new UrlMapping("abc123XY", "https://example.com", "https://example.com", false);
        ClickEvent click = new ClickEvent("abc123XY", "Mozilla", null);
        when(urlMappingRepository.findByCode("abc123XY")).thenReturn(Optional.of(mapping));
        when(clickEventRepository.countByCode("abc123XY")).thenReturn(1L);
        when(clickEventRepository.findTop20ByCodeOrderByClickedAtDesc("abc123XY")).thenReturn(List.of(click));

        AnalyticsResponse response = service.getAnalytics("abc123XY");

        assertThat(response.code()).isEqualTo("abc123XY");
        assertThat(response.clickCount()).isEqualTo(1);
        assertThat(response.recentClicks()).hasSize(1);
    }

    @Test
    void unknownCodeReturnsNotFound() {
        when(urlMappingRepository.findByCode("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAnalytics("missing"))
                .isInstanceOf(UrlNotFoundException.class);
    }
}
