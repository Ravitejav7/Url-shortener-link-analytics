package com.example.urlshortener.entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "url_mappings")
public class UrlMapping {

    @Id
    private String id;
    private String code;
    private String originalUrl;
    private String normalizedUrl;
    private boolean customAlias;
    private Instant createdAt;
    private Instant updatedAt;

    public UrlMapping() {
    }

    public UrlMapping(String code, String originalUrl, String normalizedUrl, boolean customAlias) {
        this.code = code;
        this.originalUrl = originalUrl;
        this.normalizedUrl = normalizedUrl;
        this.customAlias = customAlias;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getNormalizedUrl() {
        return normalizedUrl;
    }

    public void setNormalizedUrl(String normalizedUrl) {
        this.normalizedUrl = normalizedUrl;
    }

    public boolean isCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(boolean customAlias) {
        this.customAlias = customAlias;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
