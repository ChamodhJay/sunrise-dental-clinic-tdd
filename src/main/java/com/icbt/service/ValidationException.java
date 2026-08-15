package com.icbt.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final Map<String, String> fieldErrors;

    public ValidationException(Map<String, String> fieldErrors) {
        super("Submitted data is invalid");
        this.fieldErrors = Collections.unmodifiableMap(new LinkedHashMap<>(fieldErrors));
    }

    public Map<String, String> getFieldErrors() { return fieldErrors; }
}
