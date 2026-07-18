package com.example.urlshortener.helper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ShortCodeGeneratorTest {

    private final ShortCodeGenerator generator = new ShortCodeGenerator();

    @Test
    void generatedCodeIsEightCharacterBase62() {
        String code = generator.generate();

        assertThat(code).hasSize(8);
        assertThat(code).matches("[A-Za-z0-9]{8}");
    }
}
