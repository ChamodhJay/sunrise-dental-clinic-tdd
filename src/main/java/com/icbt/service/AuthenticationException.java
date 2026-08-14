package com.icbt.service;

public final class AuthenticationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final long retryAfterSeconds;

    public AuthenticationException(String message) {
        this(message, 0);
    }

    public AuthenticationException(String message, long retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() { return retryAfterSeconds; }
}
