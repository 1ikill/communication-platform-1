package com.sdc.user.config.security;

import com.sdc.user.domain.constants.RoleType;
import com.sdc.user.domain.model.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtTokenProvider
 */
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        
        // Set up test properties
        ReflectionTestUtils.setField(jwtTokenProvider, "secret", 
            "testSecretKeyThatIsLongEnoughForHS256Algorithm12345678");
        ReflectionTestUtils.setField(jwtTokenProvider, "expirationMs", 3600000L); // 1 hour
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshMs", 86400000L); // 24 hours
        
        // Initialize the signing key
        jwtTokenProvider.init();

        // Set up test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setRole(RoleType.USER);
    }

    @Test
    void init_Success() {
        // Arrange
        JwtTokenProvider provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "secret", 
            "testSecretKeyThatIsLongEnoughForHS256Algorithm12345678");

        // Act & Assert
        assertDoesNotThrow(() -> provider.init());
    }

    @Test
    void generateAccessToken_Success() {
        // Act
        String token = jwtTokenProvider.generateAccessToken(testUser);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void generateAccessToken_ContainsCorrectClaims() {
        // Act
        String token = jwtTokenProvider.generateAccessToken(testUser);
        Claims claims = jwtTokenProvider.parseToken(token);

        // Assert
        assertEquals("1", claims.getSubject());
        assertEquals("test@example.com", claims.get("username", String.class));
        assertEquals("USER", claims.get("role", String.class));
        assertEquals("Test User", claims.get("fullName", String.class));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void generateAccessToken_WithAdminRole() {
        // Arrange
        testUser.setRole(RoleType.ADMIN);

        // Act
        String token = jwtTokenProvider.generateAccessToken(testUser);
        Claims claims = jwtTokenProvider.parseToken(token);

        // Assert
        assertEquals("ADMIN", claims.get("role", String.class));
    }

    @Test
    void generateRefreshToken_Success() {
        // Act
        String token = jwtTokenProvider.generateRefreshToken(testUser);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void generateRefreshToken_ContainsSubject() {
        // Act
        String token = jwtTokenProvider.generateRefreshToken(testUser);
        Long userId = jwtTokenProvider.parseRefreshToken(token);

        // Assert
        assertEquals(1L, userId);
    }

    @Test
    void parseRefreshToken_Success() {
        // Arrange
        String refreshToken = jwtTokenProvider.generateRefreshToken(testUser);

        // Act
        Long userId = jwtTokenProvider.parseRefreshToken(refreshToken);

        // Assert
        assertNotNull(userId);
        assertEquals(1L, userId);
    }

    @Test
    void parseRefreshToken_DifferentUser() {
        // Arrange
        testUser.setId(123L);
        String refreshToken = jwtTokenProvider.generateRefreshToken(testUser);

        // Act
        Long userId = jwtTokenProvider.parseRefreshToken(refreshToken);

        // Assert
        assertEquals(123L, userId);
    }

    @Test
    void parseToken_Success() {
        // Arrange
        String accessToken = jwtTokenProvider.generateAccessToken(testUser);

        // Act
        Claims claims = jwtTokenProvider.parseToken(accessToken);

        // Assert
        assertNotNull(claims);
        assertEquals("1", claims.getSubject());
    }

    @Test
    void parseToken_InvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThrows(Exception.class, () -> jwtTokenProvider.parseToken(invalidToken));
    }

    @Test
    void parseRefreshToken_InvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThrows(Exception.class, () -> jwtTokenProvider.parseRefreshToken(invalidToken));
    }

    @Test
    void generateAccessToken_DifferentUsers() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@example.com");
        user1.setFullName("User One");
        user1.setRole(RoleType.USER);

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");
        user2.setFullName("User Two");
        user2.setRole(RoleType.ADMIN);

        // Act
        String token1 = jwtTokenProvider.generateAccessToken(user1);
        String token2 = jwtTokenProvider.generateAccessToken(user2);

        // Assert
        assertNotEquals(token1, token2);
        
        Claims claims1 = jwtTokenProvider.parseToken(token1);
        Claims claims2 = jwtTokenProvider.parseToken(token2);
        
        assertEquals("1", claims1.getSubject());
        assertEquals("2", claims2.getSubject());
        assertEquals("USER", claims1.get("role", String.class));
        assertEquals("ADMIN", claims2.get("role", String.class));
    }

    @Test
    void tokenExpiration_IsSet() {
        // Act
        String token = jwtTokenProvider.generateAccessToken(testUser);
        Claims claims = jwtTokenProvider.parseToken(token);

        // Assert
        assertNotNull(claims.getExpiration());
        assertNotNull(claims.getIssuedAt());
        assertTrue(claims.getExpiration().getTime() > claims.getIssuedAt().getTime());
    }

    @Test
    void refreshTokenExpiration_IsSet() {
        // Act
        String token = jwtTokenProvider.generateRefreshToken(testUser);
        Claims claims = jwtTokenProvider.parseToken(token);

        // Assert
        assertNotNull(claims.getExpiration());
        assertNotNull(claims.getIssuedAt());
        assertTrue(claims.getExpiration().getTime() > claims.getIssuedAt().getTime());
    }
}
