package com.example.urlshortener.helper;

import java.util.Set;
import java.util.regex.Pattern;

import com.example.urlshortener.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class AliasValidator {

    private static final Pattern ALIAS_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{3,32}$");
    private static final Set<String> RESERVED_ALIASES = Set.of("shorten", "analytics", "api", "actuator");

    public String validateAndNormalize(String alias) {
        if (alias == null || alias.isBlank()) {
            return null;
        }

        String trimmedAlias = alias.trim();
        if (!ALIAS_PATTERN.matcher(trimmedAlias).matches()) {
            throw new BadRequestException("Alias must be 3-32 characters and contain only letters, numbers, hyphen, or underscore");
        }

        if (RESERVED_ALIASES.contains(trimmedAlias.toLowerCase())) {
            throw new BadRequestException("Alias is reserved: " + trimmedAlias);
        }

        return trimmedAlias;
    }
}
