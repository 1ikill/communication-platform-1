package com.sdc.discord.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CurrentUser}
 */
@ExtendWith(MockitoExtension.class)
class CurrentUserTest {

    @InjectMocks
    private CurrentUser currentUser;

    private Jwt testJwt;

    @BeforeEach
    void setUp() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");
        headers.put("typ", "JWT");

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "123");
        claims.put("username", "testuser");
        claims.put("role", "USER");

        testJwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                headers,
                claims
        );
    }

    @Test
    @DisplayName("Should get user ID from JWT")
    void testGetId() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(testJwt, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long userId = currentUser.getId();

        assertEquals(123L, userId);
    }

    @Test
    @DisplayName("Should get username from JWT")
    void testGetUsername() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(testJwt, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String username = currentUser.getUsername();

        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("Should get role from JWT")
    void testGetRole() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(testJwt, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String role = currentUser.getRole();

        assertEquals("USER", role);
    }

    @Test
    @DisplayName("Should handle different user IDs")
    void testGetIdDifferentValues() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "999");
        claims.put("username", "anotheruser");
        claims.put("role", "ADMIN");

        Jwt anotherJwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                headers,
                claims
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(anotherJwt, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long userId = currentUser.getId();
        String username = currentUser.getUsername();
        String role = currentUser.getRole();

        assertEquals(999L, userId);
        assertEquals("anotheruser", username);
        assertEquals("ADMIN", role);
    }

    @Test
    @DisplayName("Should handle numeric subject in JWT")
    void testGetIdNumericSubject() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "42");

        Jwt numericJwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                headers,
                claims
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(numericJwt, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long userId = currentUser.getId();

        assertEquals(42L, userId);
    }
}
