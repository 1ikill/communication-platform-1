package com.sdc.user.service;

import com.sdc.user.config.security.JwtTokenProvider;
import com.sdc.user.domain.constants.RoleType;
import com.sdc.user.domain.dto.AuthRequestDto;
import com.sdc.user.domain.dto.UserCreateDto;
import com.sdc.user.domain.dto.UserDto;
import com.sdc.user.domain.dto.UserPatchDto;
import com.sdc.user.domain.mapper.UserMapper;
import com.sdc.user.domain.model.User;
import com.sdc.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserCreateDto createDto;
    private UserDto userDto;
    private UserPatchDto patchDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setPasswordHash("hashedPassword");
        testUser.setRole(RoleType.USER);

        createDto = new UserCreateDto();
        createDto.setUsername("testuser");
        createDto.setEmail("test@example.com");
        createDto.setFullName("Test User");
        createDto.setPassword("password123");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testuser");
        userDto.setEmail("test@example.com");
        userDto.setFullName("Test User");
        userDto.setRole(RoleType.USER);

        patchDto = new UserPatchDto();
        patchDto.setUsername("updateduser");
    }

    @Test
    void register_Success() {
        // Arrange
        when(mapper.fromCreateDto(createDto)).thenReturn(testUser);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(mapper.toDto(any(User.class))).thenReturn(userDto);

        // Act
        UserDto result = userService.register(createDto);

        // Assert
        assertNotNull(result);
        assertEquals(userDto.getUsername(), result.getUsername());
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void register_UserAlreadyExistsByEmail() {
        // Arrange
        when(mapper.fromCreateDto(createDto)).thenReturn(testUser);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.register(createDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_UserAlreadyExistsByUsername() {
        // Arrange
        when(mapper.fromCreateDto(createDto)).thenReturn(testUser);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.register(createDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createByAdmin_Success() {
        // Arrange
        when(mapper.fromCreateDto(createDto)).thenReturn(testUser);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(mapper.toDto(any(User.class))).thenReturn(userDto);

        // Act
        UserDto result = userService.createByAdmin(createDto, RoleType.ADMIN);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createByAdmin_UserAlreadyExists() {
        // Arrange
        when(mapper.fromCreateDto(createDto)).thenReturn(testUser);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> userService.createByAdmin(createDto, RoleType.ADMIN));
    }

    @Test
    void login_Success() {
        // Arrange
        AuthRequestDto authRequest = new AuthRequestDto();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(tokenProvider.generateAccessToken(testUser)).thenReturn("accessToken");
        when(tokenProvider.generateRefreshToken(testUser)).thenReturn("refreshToken");

        // Act
        Map<String, String> result = userService.login(authRequest);

        // Assert
        assertNotNull(result);
        assertEquals("accessToken", result.get("access"));
        assertEquals("refreshToken", result.get("refresh"));
        verify(tokenProvider).generateAccessToken(testUser);
        verify(tokenProvider).generateRefreshToken(testUser);
    }

    @Test
    void login_UserNotFound() {
        // Arrange
        AuthRequestDto authRequest = new AuthRequestDto();
        authRequest.setUsername("nonexistent");
        authRequest.setPassword("password123");

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.login(authRequest));
    }

    @Test
    void login_InvalidPassword() {
        // Arrange
        AuthRequestDto authRequest = new AuthRequestDto();
        authRequest.setUsername("testuser");
        authRequest.setPassword("wrongpassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.login(authRequest));
    }

    @Test
    void refresh_Success() {
        // Arrange
        when(tokenProvider.parseRefreshToken("refreshToken")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(tokenProvider.generateAccessToken(testUser)).thenReturn("newAccessToken");
        when(tokenProvider.generateRefreshToken(testUser)).thenReturn("newRefreshToken");

        // Act
        Map<String, String> result = userService.refresh("refreshToken");

        // Assert
        assertNotNull(result);
        assertEquals("newAccessToken", result.get("access"));
        assertEquals("newRefreshToken", result.get("refresh"));
    }

    @Test
    void refresh_UserNotFound() {
        // Arrange
        when(tokenProvider.parseRefreshToken("refreshToken")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.refresh("refreshToken"));
    }

    @Test
    void findAll_Success() {
        // Arrange
        List<User> users = List.of(testUser);
        when(userRepository.findAll()).thenReturn(users);
        when(mapper.toDto(any(User.class))).thenReturn(userDto);

        // Act
        List<UserDto> result = userService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void patch_Success() {
        // Arrange
        UserPatchDto beforePatch = new UserPatchDto();
        beforePatch.setUsername("testuser");
        beforePatch.setEmail("test@example.com");
        beforePatch.setRole(RoleType.USER);

        UserPatchDto afterPatch = new UserPatchDto();
        afterPatch.setUsername("updateduser");
        afterPatch.setEmail("test@example.com");
        afterPatch.setRole(RoleType.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(mapper.toPatchDto(any(User.class))).thenReturn(beforePatch, afterPatch);
        lenient().when(authService.getCurrentUserRole()).thenReturn(RoleType.ADMIN);
        when(mapper.toDto(any(User.class))).thenReturn(userDto);

        // Act
        UserDto result = userService.patch(1L, patchDto);

        // Assert
        assertNotNull(result);
        verify(mapper).merge(any(User.class), any(UserPatchDto.class));
    }

    @Test
    void patch_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.patch(1L, patchDto));
    }

    @Test
    void patch_EmailAlreadyInUse() {
        // Arrange
        patchDto.setEmail("newemail@example.com");
        
        UserPatchDto beforePatch = new UserPatchDto();
        beforePatch.setUsername("testuser");
        beforePatch.setEmail("test@example.com");
        beforePatch.setRole(RoleType.USER);

        UserPatchDto afterPatch = new UserPatchDto();
        afterPatch.setUsername("testuser");
        afterPatch.setEmail("newemail@example.com");
        afterPatch.setRole(RoleType.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(mapper.toPatchDto(any(User.class))).thenReturn(beforePatch, afterPatch);
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.patch(1L, patchDto));
    }

    @Test
    void patch_UsernameAlreadyInUse() {
        // Arrange
        UserPatchDto beforePatch = new UserPatchDto();
        beforePatch.setUsername("testuser");
        beforePatch.setEmail("test@example.com");
        beforePatch.setRole(RoleType.USER);

        UserPatchDto afterPatch = new UserPatchDto();
        afterPatch.setUsername("updateduser");
        afterPatch.setEmail("test@example.com");
        afterPatch.setRole(RoleType.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(mapper.toPatchDto(any(User.class))).thenReturn(beforePatch, afterPatch);
        when(userRepository.existsByUsername("updateduser")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.patch(1L, patchDto));
    }

    @Test
    void patch_WithPassword() {
        // Arrange
        patchDto.setPassword("newPassword");
        
        UserPatchDto beforePatch = new UserPatchDto();
        beforePatch.setUsername("testuser");
        beforePatch.setEmail("test@example.com");
        beforePatch.setRole(RoleType.USER);

        UserPatchDto afterPatch = new UserPatchDto();
        afterPatch.setUsername("updateduser");
        afterPatch.setEmail("test@example.com");
        afterPatch.setRole(RoleType.USER);
        afterPatch.setPassword("newPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(mapper.toPatchDto(any(User.class))).thenReturn(beforePatch, afterPatch);
        lenient().when(authService.getCurrentUserRole()).thenReturn(RoleType.ADMIN);
        when(passwordEncoder.encode("newPassword")).thenReturn("hashedNewPassword");
        when(mapper.toDto(any(User.class))).thenReturn(userDto);

        // Act
        UserDto result = userService.patch(1L, patchDto);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder).encode("newPassword");
    }

    @Test
    void patch_RoleChangeByNonAdmin() {
        // Arrange
        patchDto.setRole(RoleType.ADMIN);
        
        UserPatchDto beforePatch = new UserPatchDto();
        beforePatch.setUsername("testuser");
        beforePatch.setEmail("test@example.com");
        beforePatch.setRole(RoleType.USER);

        UserPatchDto afterPatch = new UserPatchDto();
        afterPatch.setUsername("testuser");
        afterPatch.setEmail("test@example.com");
        afterPatch.setRole(RoleType.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(mapper.toPatchDto(any(User.class))).thenReturn(beforePatch, afterPatch);
        when(authService.getCurrentUserRole()).thenReturn(RoleType.USER);
        when(mapper.toDto(any(User.class))).thenReturn(userDto);

        // Act
        UserDto result = userService.patch(1L, patchDto);

        // Assert
        assertNotNull(result);
        // Role should be reverted to original
        verify(authService).getCurrentUserRole();
    }

    @Test
    void getMe_Success() {
        // Arrange
        when(authService.getCurrentUserId()).thenReturn(1L);
        when(userRepository.getById(1L)).thenReturn(testUser);
        when(mapper.toDto(testUser)).thenReturn(userDto);

        // Act
        UserDto result = userService.getMe();

        // Assert
        assertNotNull(result);
        assertEquals(userDto.getUsername(), result.getUsername());
        verify(authService).getCurrentUserId();
    }
}
