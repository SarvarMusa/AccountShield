package com.codems.accountshield.common.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SecurityPaths {

    @Bean("publicPaths")
    public List<String> publicPaths() {
        return List.of(
                "/api/auth/register",
                "/api/auth/login",
                "/api/auth/verify-email",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-ui.html",
                "/api/auth/refresh"
        );
    }

    @Bean("userPaths")
    public List<String> userPaths() {
        return List.of(
                "/api/profile/**"
        );
    }

    @Bean("adminPaths")
    public List<String> adminPaths() {
        return List.of(
                "/api/users/admin/**"
        );
    }
}
