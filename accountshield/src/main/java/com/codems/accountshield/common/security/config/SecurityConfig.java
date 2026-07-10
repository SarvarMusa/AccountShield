package com.codems.accountshield.common.security.config;

import com.codems.accountshield.domain.user.entity.Role;
import com.codems.accountshield.common.security.handler.CustomAccessDeniedHandler;
import com.codems.accountshield.common.security.handler.CustomAuthenticationEntryPoint;
import com.codems.accountshield.common.security.provider.AccountShieldAuthenticationProvider;
import com.codems.accountshield.common.security.filter.JwtAuthenticationFilter;
import com.codems.accountshield.common.security.properties.CorsConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigProperties configProperties;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    @Qualifier("publicPaths")
    private final List<String> publicPaths;
    @Qualifier("adminPaths")
    private final List<String> adminPaths;

    @Qualifier("userPaths")
    private final List<String> userPaths;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requestMatchers -> {
                    publicPaths.forEach(path -> requestMatchers.requestMatchers(path).permitAll());
                    userPaths.forEach(path -> requestMatchers.requestMatchers(path).hasRole(Role.USER.name()));
                    adminPaths.forEach(path -> requestMatchers.requestMatchers(path).hasRole(Role.ADMIN.name()));
                    requestMatchers.anyRequest().authenticated();
                })

                .sessionManagement(
                        sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationEventPublisher authenticationEventPublisher(ApplicationEventPublisher publisher) {
        return new DefaultAuthenticationEventPublisher(publisher);
    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AccountShieldAuthenticationProvider authenticationProvider,
            AuthenticationEventPublisher authenticationEventPublisher
    ) {
        ProviderManager authenticationManager = new ProviderManager(authenticationProvider);
        authenticationManager.setAuthenticationEventPublisher(authenticationEventPublisher);
        return authenticationManager;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
        config.setAllowedHeaders(Arrays.asList(configProperties.getAllowedHeaders()));
        config.setAllowedMethods(Arrays.asList(configProperties.getAllowedMethods()));
        config.setAllowedOrigins(Arrays.asList(configProperties.getAllowedOrigins()));
        config.setAllowCredentials(configProperties.isAllowCredentials());
        config.setMaxAge(configProperties.getMaxAge());
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
