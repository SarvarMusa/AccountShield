package com.codems.accountshield.common.security.service;

import com.codems.accountshield.domain.user.entity.User;
import com.codems.accountshield.common.security.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_REFRESH_FAMILY = "family";
    private static final String ACCESS_TOKEN = "access";
    private static final String REFRESH_TOKEN = "refresh";

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim(CLAIM_TYPE, ACCESS_TOKEN)
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(secretKey)
                .compact();
    }

    public String generateToken(User user) {
        return generateAccessToken(user);
    }

    public String generateRefreshToken(User user, String jti, String familyId) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + jwtProperties.getRefreshExpiration());

        return Jwts.builder()
                .id(jti)
                .subject(user.getEmail())
                .claim(CLAIM_TYPE, REFRESH_TOKEN)
                .claim(CLAIM_REFRESH_FAMILY, familyId)
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return claims(token).getSubject();
    }

    public boolean isTokenValid(String token, User user) {
        return isAccessTokenValid(token, user);
    }

    public boolean isAccessTokenValid(String token, User user) {
        String username = extractUsername(token);
        return username.equals(user.getEmail())
                && ACCESS_TOKEN.equalsIgnoreCase(extractTokenType(token))
                && !claims(token).getExpiration().before(new Date());
    }

    public boolean isRefreshTokenValid(String token, User user) {
        String username = extractUsername(token);
        return username.equals(user.getEmail())
                && REFRESH_TOKEN.equalsIgnoreCase(extractTokenType(token))
                && !claims(token).getExpiration().before(new Date());
    }

    public long refreshExpirationMillis() {
        return jwtProperties.getRefreshExpiration();
    }

    public String extractTokenType(String token) {
        Object type = claims(token).get(CLAIM_TYPE);
        return type == null ? ACCESS_TOKEN : type.toString();
    }

    public String extractJti(String token) {
        return claims(token).getId();
    }

    public String extractFamilyId(String token) {
        Object family = claims(token).get(CLAIM_REFRESH_FAMILY);
        return family == null ? null : family.toString();
    }

    private Claims claims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
