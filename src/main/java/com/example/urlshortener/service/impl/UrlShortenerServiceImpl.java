package com.example.urlshortener.service.impl;

import com.example.urlshortener.dto.request.ShortenUrlRequest;
import com.example.urlshortener.dto.response.ShortenUrlResponse;
import com.example.urlshortener.entity.UrlMapping;
import com.example.urlshortener.exception.AliasAlreadyExistsException;
import com.example.urlshortener.exception.CodeGenerationException;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.helper.AliasValidator;
import com.example.urlshortener.helper.ShortCodeGenerator;
import com.example.urlshortener.helper.ShortUrlBuilder;
import com.example.urlshortener.helper.UrlNormalizer;
import com.example.urlshortener.repository.UrlMappingRepository;
import com.example.urlshortener.service.ShortenUrlResult;
import com.example.urlshortener.service.UrlShortenerService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {

    private static final int MAX_CODE_GENERATION_ATTEMPTS = 5;

    private final UrlMappingRepository urlMappingRepository;
    private final UrlNormalizer urlNormalizer;
    private final AliasValidator aliasValidator;
    private final ShortCodeGenerator shortCodeGenerator;
    private final ShortUrlBuilder shortUrlBuilder;

    public UrlShortenerServiceImpl(
            UrlMappingRepository urlMappingRepository,
            UrlNormalizer urlNormalizer,
            AliasValidator aliasValidator,
            ShortCodeGenerator shortCodeGenerator,
            ShortUrlBuilder shortUrlBuilder
    ) {
        this.urlMappingRepository = urlMappingRepository;
        this.urlNormalizer = urlNormalizer;
        this.aliasValidator = aliasValidator;
        this.shortCodeGenerator = shortCodeGenerator;
        this.shortUrlBuilder = shortUrlBuilder;
    }

    @Override
    public ShortenUrlResult shorten(ShortenUrlRequest request) {
        String normalizedUrl = urlNormalizer.validateAndNormalize(request == null ? null : request.url());
        String alias = aliasValidator.validateAndNormalize(request == null ? null : request.alias());

        if (alias == null) {
            return shortenWithGeneratedCode(normalizedUrl);
        }
        return shortenWithCustomAlias(normalizedUrl, alias);
    }

    @Override
    public String getOriginalUrl(String code) {
        return urlMappingRepository.findByCode(code)
                .map(UrlMapping::getOriginalUrl)
                .orElseThrow(() -> new UrlNotFoundException(code));
    }

    private ShortenUrlResult shortenWithGeneratedCode(String normalizedUrl) {
        return urlMappingRepository.findFirstByNormalizedUrlAndCustomAliasFalse(normalizedUrl)
                .map(existing -> new ShortenUrlResult(false, toResponse(existing)))
                .orElseGet(() -> createGeneratedMapping(normalizedUrl));
    }

    private ShortenUrlResult createGeneratedMapping(String normalizedUrl) {
        for (int attempt = 1; attempt <= MAX_CODE_GENERATION_ATTEMPTS; attempt++) {
            String code = shortCodeGenerator.generate();
            try {
                UrlMapping saved = urlMappingRepository.save(new UrlMapping(code, normalizedUrl, normalizedUrl, false));
                return new ShortenUrlResult(true, toResponse(saved));
            } catch (DuplicateKeyException ignored) {
                // Collision is unlikely, but the unique index is the final guard. Try another code.
            }
        }
        throw new CodeGenerationException("Unable to generate a unique short code");
    }

    private ShortenUrlResult shortenWithCustomAlias(String normalizedUrl, String alias) {
        if (urlMappingRepository.existsByCode(alias)) {
            throw new AliasAlreadyExistsException(alias);
        }

        try {
            UrlMapping saved = urlMappingRepository.save(new UrlMapping(alias, normalizedUrl, normalizedUrl, true));
            return new ShortenUrlResult(true, toResponse(saved));
        } catch (DuplicateKeyException ex) {
            throw new AliasAlreadyExistsException(alias);
        }
    }

    private ShortenUrlResponse toResponse(UrlMapping mapping) {
        return new ShortenUrlResponse(
                mapping.getCode(),
                shortUrlBuilder.build(mapping.getCode()),
                mapping.getOriginalUrl()
        );
    }
}
