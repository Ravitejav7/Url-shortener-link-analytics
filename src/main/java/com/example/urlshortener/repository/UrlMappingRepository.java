package com.example.urlshortener.repository;

import java.util.Optional;

import com.example.urlshortener.entity.UrlMapping;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UrlMappingRepository extends MongoRepository<UrlMapping, String> {

    Optional<UrlMapping> findByCode(String code);

    boolean existsByCode(String code);

    Optional<UrlMapping> findFirstByNormalizedUrlAndCustomAliasFalse(String normalizedUrl);
}
