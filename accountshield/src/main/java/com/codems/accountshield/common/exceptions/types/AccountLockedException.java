package com.codems.accountshield.common.exceptions.types;

import org.springframework.http.HttpStatus;

public class AccountLockedException extends BaseException {
    private static final String message = "Your account has been locked";

    public AccountLockedException() {
        super(message, HttpStatus.LOCKED);
    }
}
