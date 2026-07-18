package com.example.urlshortener.helper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import com.example.urlshortener.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class UrlNormalizer {

    public String validateAndNormalize(String url) {
        if (url == null || url.isBlank()) {
            throw new BadRequestException("URL is required");
        }

        String trimmedUrl = url.trim();
        URI uri;
        try {
            uri = new URI(trimmedUrl).normalize();
        } catch (URISyntaxException ex) {
            throw new BadRequestException("URL is malformed");
        }

        String scheme = uri.getScheme();
        String host = uri.getHost();
        if (scheme == null || host == null) {
            throw new BadRequestException("URL must be absolute and include a host");
        }

        String normalizedScheme = scheme.toLowerCase(Locale.ROOT);
        if (!normalizedScheme.equals("http") && !normalizedScheme.equals("https")) {
            throw new BadRequestException("Only http and https URLs are supported");
        }

        try {
            URI normalizedUri = new URI(
                    normalizedScheme,
                    uri.getUserInfo(),
                    host.toLowerCase(Locale.ROOT),
                    uri.getPort(),
                    uri.getPath(),
                    uri.getQuery(),
                    uri.getFragment()
            );
            return normalizedUri.toString();
        } catch (URISyntaxException ex) {
            throw new BadRequestException("URL is malformed");
        }
    }
}
