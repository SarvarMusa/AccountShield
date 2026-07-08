package com.codems.accountshield.common.exceptions.types;

import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends BaseException {

    public InvalidRefreshTokenException() {
        super("Invalid refresh token", HttpStatus.UNAUTHORIZED);
    }
}
