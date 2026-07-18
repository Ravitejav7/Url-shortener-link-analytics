package com.example.urlshortener.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.urlshortener.exception.BadRequestException;
import org.junit.jupiter.api.Test;

class AliasValidatorTest {

    private final AliasValidator validator = new AliasValidator();

    @Test
    void acceptsUrlSafeAliases() {
        assertThat(validator.validateAndNormalize(" my-demo_123 ")).isEqualTo("my-demo_123");
    }

    @Test
    void treatsMissingAliasAsNull() {
        assertThat(validator.validateAndNormalize(null)).isNull();
        assertThat(validator.validateAndNormalize(" ")).isNull();
    }

    @Test
    void rejectsInvalidAndReservedAliases() {
        assertThatThrownBy(() -> validator.validateAndNormalize("ab"))
                .isInstanceOf(BadRequestException.class);

        assertThatThrownBy(() -> validator.validateAndNormalize("bad alias"))
                .isInstanceOf(BadRequestException.class);

        assertThatThrownBy(() -> validator.validateAndNormalize("analytics"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("reserved");
    }
}
