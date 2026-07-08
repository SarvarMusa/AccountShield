package com.codems.accountshield.common.exceptions.types;

import org.springframework.http.HttpStatus;

import com.codems.accountshield.domain.auth.verification.EmailVerificationMessages;

public class EmailVerificationException extends BaseException {

    public EmailVerificationException() {
        super(EmailVerificationMessages.INVALID_CODE, HttpStatus.BAD_REQUEST);
    }
}
