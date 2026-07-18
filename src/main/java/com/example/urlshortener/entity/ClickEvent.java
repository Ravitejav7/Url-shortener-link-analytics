package com.example.urlshortener.entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "click_events")
public class ClickEvent {

    @Id
    private String id;
    private String code;
    private Instant clickedAt;
    private String userAgent;
    private String referrer;

    public ClickEvent() {
    }

    public ClickEvent(String code, String userAgent, String referrer) {
        this.code = code;
        this.clickedAt = Instant.now();
        this.userAgent = userAgent;
        this.referrer = referrer;
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

    public Instant getClickedAt() {
        return clickedAt;
    }

    public void setClickedAt(Instant clickedAt) {
        this.clickedAt = clickedAt;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }
}
