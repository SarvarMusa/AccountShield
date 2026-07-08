package com.codems.accountshield.common.exceptions.types;

import org.springframework.http.HttpStatus;

import java.util.Map;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final Map<String, String> validationErrors;

    protected BaseException(String message, HttpStatus status) {
        this(message, status, null);
    }

    protected BaseException(String message, HttpStatus status, Map<String, String> validationErrors) {
        super(message);
        this.status = status;
        this.validationErrors = validationErrors;
    }

    protected BaseException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.validationErrors = null;
    }
}
