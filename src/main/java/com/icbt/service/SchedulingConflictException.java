package com.icbt.service;

public final class SchedulingConflictException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SchedulingConflictException(String message) {
        super(message);
    }

    public SchedulingConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
