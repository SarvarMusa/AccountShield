package com.codems.accountshield.common.constants;

import org.springframework.http.HttpHeaders;

public final class ApplicationConstants {

    public static final String API_PATH_PREFIX = "/api";
    public static final String API_VERSION = "1.0";
    public static final String AUTH_HEADER_NAME = HttpHeaders.AUTHORIZATION;
    public static final String EMAIL_VERIFICATION_CACHE_NAME = "email-verification-codes";
    public static final String AUTH_HEADER_PREFIX = "Bearer ";
    public static final String SYSTEM = "SYSTEM";


    private ApplicationConstants() {
        throw new UnsupportedOperationException("ApplicationConstants cannot be instantiated");
    }
}
