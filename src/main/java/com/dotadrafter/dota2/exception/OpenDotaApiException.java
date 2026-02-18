package com.dotadrafter.dota2.exception;

/**
 * Custom exception for OpenDota API errors.
 * Provides descriptive error messages for HTTP errors and network failures.
 */
public class OpenDotaApiException extends RuntimeException {

    public OpenDotaApiException(String message) {
        super(message);
    }

    public OpenDotaApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
