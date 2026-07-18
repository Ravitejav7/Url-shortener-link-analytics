package com.example.urlshortener.exception;

public class UrlNotFoundException extends RuntimeException {

    public UrlNotFoundException(String code) {
        super("Short code not found: " + code);
    }
}
