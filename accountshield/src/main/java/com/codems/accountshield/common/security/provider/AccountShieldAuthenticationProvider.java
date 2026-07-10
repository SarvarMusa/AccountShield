package com.codems.accountshield.common.security.provider;

import com.codems.accountshield.domain.user.entity.User;
import com.codems.accountshield.domain.user.repository.UserRepository;
import com.codems.accountshield.domain.user.service.LoginAttemptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.lang.Nullable;

import java.util.List;

@Component
@Slf4j
public class AccountShieldAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    @Nullable
    private final CompromisedPasswordChecker compromisedPasswordChecker;

    public AccountShieldAuthenticationProvider(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            LoginAttemptService loginAttemptService,
            @Nullable CompromisedPasswordChecker compromisedPasswordChecker
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.compromisedPasswordChecker = compromisedPasswordChecker;
    }

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName().trim();
        String rawPassword = authentication.getCredentials().toString();
        log.debug("Authenticating user {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        loginAttemptService.unlockIfExpired(user);
        preAuthenticationChecks(user);

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            log.warn("Invalid password provided for {}", email);
            throw new BadCredentialsException("Invalid email or password");
        }

        checkCompromisedPassword(rawPassword);

        log.info("Authentication succeeded for {}", email);
        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    private void preAuthenticationChecks(User user) {

        if (user.isAccountLocked()) {
            log.warn("Failed to authenticate since user account is locked: {}", user.getEmail());
            throw new LockedException("User account is locked for 1 hour after 5 failed login attempts");
        }

        if (!user.isEmailVerified()) {
            log.warn("Failed to authenticate since user email is not verified: {}", user.getEmail());
            throw new DisabledException("Email address is not verified. Please verify your email before logging in");
        }
    }

    private void checkCompromisedPassword(String rawPassword) {
        boolean isPasswordCompromised = compromisedPasswordChecker != null
                && compromisedPasswordChecker.check(rawPassword).isCompromised();

        if (isPasswordCompromised) {
            throw new CompromisedPasswordException("The provided password is compromised, please change your password");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
