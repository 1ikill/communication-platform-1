package com.sdc.gmail.config.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrentUserTest {

    private CurrentUser currentUser;
    private SecurityContext securityContext;
    private Authentication authentication;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        currentUser = new CurrentUser();
        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        
        final Map<String, Object> claims = new HashMap<>();
        claims.put("username", "testuser");
        claims.put("role", "ADMIN");
        
        jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),
                claims
        );
        jwt = Jwt.withTokenValue("token-value")
                .header("alg", "RS256")
                .claim("sub", "123")
                .claim("username", "testuser")
                .claim("role", "ADMIN")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(authentication.getPrincipal()).thenReturn(jwt);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetId() {
        final Long userId = currentUser.getId();

        assertNotNull(userId);
        assertEquals(123L, userId);
    }

    @Test
    void testGetUsername() {
        final String username = currentUser.getUsername();

        assertNotNull(username);
        assertEquals("testuser", username);
    }

    @Test
    void testGetRole() {
        final String role = currentUser.getRole();

        assertNotNull(role);
        assertEquals("ADMIN", role);
    }

    @Test
    void testGetId_DifferentUser() {
        final Jwt newJwt = Jwt.withTokenValue("token-value-2")
                .header("alg", "RS256")
                .claim("sub", "456")
                .claim("username", "anotheruser")
                .claim("role", "USER")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(authentication.getPrincipal()).thenReturn(newJwt);

        final Long userId = currentUser.getId();

        assertEquals(456L, userId);
    }

    @Test
    void testGetUsername_DifferentUser() {
        final Jwt newJwt = Jwt.withTokenValue("token-value-2")
                .header("alg", "RS256")
                .claim("sub", "456")
                .claim("username", "anotheruser")
                .claim("role", "USER")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(authentication.getPrincipal()).thenReturn(newJwt);

        final String username = currentUser.getUsername();

        assertEquals("anotheruser", username);
    }

    @Test
    void testGetRole_DifferentRole() {
        final Jwt newJwt = Jwt.withTokenValue("token-value-2")
                .header("alg", "RS256")
                .claim("sub", "456")
                .claim("username", "anotheruser")
                .claim("role", "MODERATOR")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(authentication.getPrincipal()).thenReturn(newJwt);

        final String role = currentUser.getRole();

        assertEquals("MODERATOR", role);
    }

    @Test
    void testMultipleCalls() {
        final Long userId1 = currentUser.getId();
        final Long userId2 = currentUser.getId();
        final String username1 = currentUser.getUsername();
        final String username2 = currentUser.getUsername();

        assertEquals(userId1, userId2);
        assertEquals(username1, username2);
    }
}
