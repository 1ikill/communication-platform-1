package com.sdc.user.repository;

import com.sdc.user.domain.constants.RoleType;
import com.sdc.user.domain.exception.NotFoundException;
import com.sdc.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserRepository
 */
@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setPasswordHash("hashedPassword");
        testUser.setRole(RoleType.USER);
    }

    @Test
    void findByUsername_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userRepository.findByUsername("testuser");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void findByUsername_NotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userRepository.findByUsername("nonexistent");

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void findByEmail_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userRepository.findByEmail("test@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void findByEmail_NotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void existsByEmail_True() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act
        boolean exists = userRepository.existsByEmail("test@example.com");

        // Assert
        assertTrue(exists);
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void existsByEmail_False() {
        // Arrange
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // Act
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Assert
        assertFalse(exists);
        verify(userRepository).existsByEmail("nonexistent@example.com");
    }

    @Test
    void existsByUsername_True() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act
        boolean exists = userRepository.existsByUsername("testuser");

        // Assert
        assertTrue(exists);
        verify(userRepository).existsByUsername("testuser");
    }

    @Test
    void existsByUsername_False() {
        // Arrange
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        // Act
        boolean exists = userRepository.existsByUsername("nonexistent");

        // Assert
        assertFalse(exists);
        verify(userRepository).existsByUsername("nonexistent");
    }

    @Test
    void getById_Success() {
        // Arrange
        testUser.setId(1L);
        when(userRepository.getById(1L)).thenReturn(testUser);

        // Act
        User result = userRepository.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        verify(userRepository).getById(1L);
    }

    @Test
    void getById_ThrowsNotFoundException() {
        // Arrange
        when(userRepository.getById(999L)).thenThrow(new NotFoundException("Can't find User with id=999"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userRepository.getById(999L));
        verify(userRepository).getById(999L);
    }
}
