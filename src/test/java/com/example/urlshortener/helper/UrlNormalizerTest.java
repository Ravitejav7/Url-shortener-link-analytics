package com.example.urlshortener.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.urlshortener.exception.BadRequestException;
import org.junit.jupiter.api.Test;

class UrlNormalizerTest {

    private final UrlNormalizer normalizer = new UrlNormalizer();

    @Test
    void acceptsHttpAndHttpsUrls() {
        assertThat(normalizer.validateAndNormalize(" HTTPS://Example.COM/a/../b?q=1 "))
                .isEqualTo("https://example.com/b?q=1");
    }

    @Test
    void rejectsUnsupportedSchemes() {
        assertThatThrownBy(() -> normalizer.validateAndNormalize("javascript:alert(1)"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("absolute");

        assertThatThrownBy(() -> normalizer.validateAndNormalize("mailto:test@example.com"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void rejectsRelativeOrBlankUrls() {
        assertThatThrownBy(() -> normalizer.validateAndNormalize("/local/path"))
                .isInstanceOf(BadRequestException.class);

        assertThatThrownBy(() -> normalizer.validateAndNormalize(" "))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("required");
    }
}
