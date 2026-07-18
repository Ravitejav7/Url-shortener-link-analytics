package com.example.urlshortener.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.example.urlshortener.dto.request.ShortenUrlRequest;
import com.example.urlshortener.entity.UrlMapping;
import com.example.urlshortener.exception.AliasAlreadyExistsException;
import com.example.urlshortener.exception.CodeGenerationException;
import com.example.urlshortener.helper.AliasValidator;
import com.example.urlshortener.helper.ShortCodeGenerator;
import com.example.urlshortener.helper.ShortUrlBuilder;
import com.example.urlshortener.helper.UrlNormalizer;
import com.example.urlshortener.repository.UrlMappingRepository;
import com.example.urlshortener.service.ShortenUrlResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceImplTest {

    @Mock
    private UrlMappingRepository urlMappingRepository;

    @Spy
    private UrlNormalizer urlNormalizer = new UrlNormalizer();

    @Spy
    private AliasValidator aliasValidator = new AliasValidator();

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    @Mock
    private ShortUrlBuilder shortUrlBuilder;

    @InjectMocks
    private UrlShortenerServiceImpl service;

    @Test
    void duplicateGeneratedUrlReturnsExistingCode() {
        UrlMapping existing = new UrlMapping("abc123XY", "https://example.com", "https://example.com", false);
        when(urlMappingRepository.findFirstByNormalizedUrlAndCustomAliasFalse("https://example.com"))
                .thenReturn(Optional.of(existing));
        when(shortUrlBuilder.build("abc123XY")).thenReturn("http://localhost:8080/abc123XY");

        ShortenUrlResult result = service.shorten(new ShortenUrlRequest("https://example.com", null));

        assertThat(result.created()).isFalse();
        assertThat(result.response().code()).isEqualTo("abc123XY");
    }

    @Test
    void customAliasCreatesRequestedCode() {
        UrlMapping saved = new UrlMapping("my-demo", "https://example.com", "https://example.com", true);
        when(urlMappingRepository.existsByCode("my-demo")).thenReturn(false);
        when(urlMappingRepository.save(org.mockito.ArgumentMatchers.any(UrlMapping.class))).thenReturn(saved);
        when(shortUrlBuilder.build("my-demo")).thenReturn("http://localhost:8080/my-demo");

        ShortenUrlResult result = service.shorten(new ShortenUrlRequest("https://example.com", "my-demo"));

        assertThat(result.created()).isTrue();
        assertThat(result.response().code()).isEqualTo("my-demo");
    }

    @Test
    void existingCustomAliasReturnsConflict() {
        when(urlMappingRepository.existsByCode("my-demo")).thenReturn(true);

        assertThatThrownBy(() -> service.shorten(new ShortenUrlRequest("https://example.com", "my-demo")))
                .isInstanceOf(AliasAlreadyExistsException.class);
    }

    @Test
    void generatedCodeRetriesOnCollision() {
        UrlMapping saved = new UrlMapping("second22", "https://example.com", "https://example.com", false);
        when(urlMappingRepository.findFirstByNormalizedUrlAndCustomAliasFalse("https://example.com"))
                .thenReturn(Optional.empty());
        when(shortCodeGenerator.generate()).thenReturn("first111", "second22");
        when(urlMappingRepository.save(org.mockito.ArgumentMatchers.any(UrlMapping.class)))
                .thenThrow(new DuplicateKeyException("duplicate"))
                .thenReturn(saved);
        when(shortUrlBuilder.build("second22")).thenReturn("http://localhost:8080/second22");

        ShortenUrlResult result = service.shorten(new ShortenUrlRequest("https://example.com", null));

        assertThat(result.created()).isTrue();
        assertThat(result.response().code()).isEqualTo("second22");
    }

    @Test
    void generatedCodeFailsAfterRetryLimit() {
        when(urlMappingRepository.findFirstByNormalizedUrlAndCustomAliasFalse("https://example.com"))
                .thenReturn(Optional.empty());
        when(shortCodeGenerator.generate()).thenReturn("samecode");
        when(urlMappingRepository.save(org.mockito.ArgumentMatchers.any(UrlMapping.class)))
                .thenThrow(new DuplicateKeyException("duplicate"));

        assertThatThrownBy(() -> service.shorten(new ShortenUrlRequest("https://example.com", null)))
                .isInstanceOf(CodeGenerationException.class);
    }
}
