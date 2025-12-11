package com.sdc.user.config.filter;

import com.sdc.user.config.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JwtAuthenticationFilter
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private Claims testClaims;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider);
        SecurityContextHolder.clearContext();
        
        testClaims = new DefaultClaims();
        testClaims.setSubject("1");
        testClaims.put("role", "USER");
    }

    @Test
    void doFilterInternal_ValidToken_Success() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtTokenProvider.parseToken("validToken")).thenReturn(testClaims);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("1", auth.getPrincipal());
        assertEquals(1, auth.getAuthorities().size());
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_NoAuthorizationHeader() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(jwtTokenProvider, never()).parseToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_NoBearerPrefix() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(jwtTokenProvider, never()).parseToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_InvalidToken() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        when(jwtTokenProvider.parseToken("invalidToken")).thenThrow(new RuntimeException("Invalid token"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_AdminRole() throws ServletException, IOException {
        // Arrange
        testClaims.put("role", "ADMIN");
        when(request.getHeader("Authorization")).thenReturn("Bearer adminToken");
        when(jwtTokenProvider.parseToken("adminToken")).thenReturn(testClaims);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("1", auth.getPrincipal());
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_DifferentUserId() throws ServletException, IOException {
        // Arrange
        testClaims.setSubject("123");
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtTokenProvider.parseToken("validToken")).thenReturn(testClaims);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("123", auth.getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_EmptyAuthorizationHeader() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(jwtTokenProvider, never()).parseToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_TokenWithWhitespace() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer  validToken");
        when(jwtTokenProvider.parseToken(" validToken")).thenReturn(testClaims);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_NullPointerException() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtTokenProvider.parseToken("validToken")).thenThrow(new NullPointerException());

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_AuthenticationDetailsSet() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtTokenProvider.parseToken("validToken")).thenReturn(testClaims);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertNotNull(auth.getDetails());
        verify(filterChain).doFilter(request, response);
    }
}
