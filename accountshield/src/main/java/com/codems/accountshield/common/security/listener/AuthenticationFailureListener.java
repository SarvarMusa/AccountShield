package com.codems.accountshield.common.security.listener;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import com.codems.accountshield.domain.user.service.LoginAttemptService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFailureListener {

    private final LoginAttemptService loginAttemptService;

    @EventListener
    public void onBadCredentials(AuthenticationFailureBadCredentialsEvent event) {
        log.debug("Authentication failure event received for {}", event.getAuthentication().getName());
        loginAttemptService.recordFailedLogin(event.getAuthentication().getName());
    }
}
