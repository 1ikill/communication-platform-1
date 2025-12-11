package com.sdc.user.service;

import com.sdc.user.config.security.JwtTokenProvider;
import com.sdc.user.domain.constants.RoleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthService authService;

    private Claims testClaims;

    @BeforeEach
    void setUp() {
        testClaims = new DefaultClaims();
        testClaims.setSubject("1");
        testClaims.put("role", "USER");
    }

    @Test
    void getCurrentUserRole_Success() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtTokenProvider.parseToken("validToken")).thenReturn(testClaims);

        // Act
        RoleType result = authService.getCurrentUserRole();

        // Assert
        assertNotNull(result);
        assertEquals(RoleType.USER, result);
        verify(jwtTokenProvider).parseToken("validToken");
    }

    @Test
    void getCurrentUserRole_NoAuthorizationHeader() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        RoleType result = authService.getCurrentUserRole();

        // Assert
        assertNull(result);
        verify(jwtTokenProvider, never()).parseToken(anyString());
    }

    @Test
    void getCurrentUserRole_EmptyAuthorizationHeader() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("");

        // Act
        RoleType result = authService.getCurrentUserRole();

        // Assert
        assertNull(result);
        verify(jwtTokenProvider, never()).parseToken(anyString());
    }

    @Test
    void getCurrentUserRole_NoBearerPrefix() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        // Act
        RoleType result = authService.getCurrentUserRole();

        // Assert
        assertNull(result);
        verify(jwtTokenProvider, never()).parseToken(anyString());
    }

    @Test
    void getCurrentUserRole_NoRoleInToken() {
        // Arrange
        Claims claimsWithoutRole = new DefaultClaims();
        claimsWithoutRole.setSubject("1");
        
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtTokenProvider.parseToken("validToken")).thenReturn(claimsWithoutRole);

        // Act
        RoleType result = authService.getCurrentUserRole();

        // Assert
        assertNull(result);
    }

    @Test
    void getCurrentUserRole_AdminRole() {
        // Arrange
        testClaims.put("role", "ADMIN");
        when(request.getHeader("Authorization")).thenReturn("Bearer adminToken");
        when(jwtTokenProvider.parseToken("adminToken")).thenReturn(testClaims);

        // Act
        RoleType result = authService.getCurrentUserRole();

        // Assert
        assertNotNull(result);
        assertEquals(RoleType.ADMIN, result);
    }

    @Test
    void getCurrentUserId_Success() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtTokenProvider.parseToken("validToken")).thenReturn(testClaims);

        // Act
        Long result = authService.getCurrentUserId();

        // Assert
        assertNotNull(result);
        assertEquals(1L, result);
        verify(jwtTokenProvider).parseToken("validToken");
    }

    @Test
    void getCurrentUserId_NoAuthorizationHeader() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        Long result = authService.getCurrentUserId();

        // Assert
        assertNull(result);
        verify(jwtTokenProvider, never()).parseToken(anyString());
    }

    @Test
    void getCurrentUserId_EmptyAuthorizationHeader() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("");

        // Act
        Long result = authService.getCurrentUserId();

        // Assert
        assertNull(result);
        verify(jwtTokenProvider, never()).parseToken(anyString());
    }

    @Test
    void getCurrentUserId_NoBearerPrefix() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        // Act
        Long result = authService.getCurrentUserId();

        // Assert
        assertNull(result);
        verify(jwtTokenProvider, never()).parseToken(anyString());
    }

    @Test
    void getCurrentUserId_DifferentUserId() {
        // Arrange
        testClaims.setSubject("123");
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtTokenProvider.parseToken("validToken")).thenReturn(testClaims);

        // Act
        Long result = authService.getCurrentUserId();

        // Assert
        assertNotNull(result);
        assertEquals(123L, result);
    }
}
