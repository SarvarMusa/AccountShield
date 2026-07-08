package com.codems.accountshield.common.exceptions.types;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class ValidationExceptions extends BaseException {
    private static final String message = "Validation failed";

    public ValidationExceptions(Map<String, String> validationErrors) {
        super(message, HttpStatus.BAD_REQUEST, validationErrors);
    }
}
