package com.sdc.ai.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentUserTest {

    @InjectMocks
    private CurrentUser currentUser;

    private Jwt jwt;
    private SecurityContext securityContext;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
        
        jwt = new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "HS256"),
            Map.of(
                "sub", "12345",
                "username", "testuser",
                "role", "ADMIN",
                "fullName", "Test User"
            )
        );
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
    }

    @Test
    void getId_ShouldReturnUserIdFromJwt() {
        // Act
        Long userId = currentUser.getId();

        // Assert
        assertNotNull(userId);
        assertEquals(12345L, userId);
    }

    @Test
    void getUsername_ShouldReturnUsernameFromJwt() {
        // Act
        String username = currentUser.getUsername();

        // Assert
        assertNotNull(username);
        assertEquals("testuser", username);
    }

    @Test
    void getRole_ShouldReturnRoleFromJwt() {
        // Act
        String role = currentUser.getRole();

        // Assert
        assertNotNull(role);
        assertEquals("ADMIN", role);
    }

    @Test
    void getFullName_ShouldReturnFullNameFromJwt() {
        // Act
        String fullName = currentUser.getFullName();

        // Assert
        assertNotNull(fullName);
        assertEquals("Test User", fullName);
    }

    @Test
    void getId_WithDifferentSubject_ShouldReturnCorrectId() {
        // Arrange
        Jwt differentJwt = new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "HS256"),
            Map.of("sub", "99999")
        );
        when(authentication.getPrincipal()).thenReturn(differentJwt);

        // Act
        Long userId = currentUser.getId();

        // Assert
        assertEquals(99999L, userId);
    }

    @Test
    void getUsername_WithNullUsername_ShouldReturnNull() {
        // Arrange
        Jwt jwtWithoutUsername = new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "HS256"),
            Map.of("sub", "12345")
        );
        when(authentication.getPrincipal()).thenReturn(jwtWithoutUsername);

        // Act
        String username = currentUser.getUsername();

        // Assert
        assertNull(username);
    }

    @Test
    void getRole_WithNullRole_ShouldReturnNull() {
        // Arrange
        Jwt jwtWithoutRole = new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "HS256"),
            Map.of("sub", "12345")
        );
        when(authentication.getPrincipal()).thenReturn(jwtWithoutRole);

        // Act
        String role = currentUser.getRole();

        // Assert
        assertNull(role);
    }

    @Test
    void getFullName_WithNullFullName_ShouldReturnNull() {
        // Arrange
        Jwt jwtWithoutFullName = new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "HS256"),
            Map.of("sub", "12345")
        );
        when(authentication.getPrincipal()).thenReturn(jwtWithoutFullName);

        // Act
        String fullName = currentUser.getFullName();

        // Assert
        assertNull(fullName);
    }
}
