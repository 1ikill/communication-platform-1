package com.sdc.user.controller;

import com.sdc.user.domain.constants.RoleType;
import com.sdc.user.domain.dto.AuthRequestDto;
import com.sdc.user.domain.dto.UserCreateDto;
import com.sdc.user.domain.dto.UserDto;
import com.sdc.user.domain.dto.UserPatchDto;
import com.sdc.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserController
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService service;

    @InjectMocks
    private UserController userController;

    private UserCreateDto createDto;
    private UserDto userDto;
    private AuthRequestDto authRequestDto;
    private UserPatchDto patchDto;

    @BeforeEach
    void setUp() {
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

        authRequestDto = new AuthRequestDto();
        authRequestDto.setUsername("testuser");
        authRequestDto.setPassword("password123");

        patchDto = new UserPatchDto();
        patchDto.setUsername("updateduser");
    }

    @Test
    void register_Success() {
        // Arrange
        when(service.register(any(UserCreateDto.class))).thenReturn(userDto);

        // Act
        UserDto result = userController.register(createDto);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(service).register(createDto);
    }

    @Test
    void login_Success() {
        // Arrange
        Map<String, String> tokens = Map.of(
            "access", "accessToken",
            "refresh", "refreshToken"
        );
        when(service.login(any(AuthRequestDto.class))).thenReturn(tokens);

        // Act
        Map<String, String> result = userController.login(authRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals("accessToken", result.get("access"));
        assertEquals("refreshToken", result.get("refresh"));
        verify(service).login(authRequestDto);
    }

    @Test
    void refresh_Success() {
        // Arrange
        String refreshToken = "validRefreshToken";
        Map<String, String> tokens = Map.of(
            "access", "newAccessToken",
            "refresh", "newRefreshToken"
        );
        when(service.refresh(anyString())).thenReturn(tokens);

        // Act
        Map<String, String> result = userController.refresh(refreshToken);

        // Assert
        assertNotNull(result);
        assertEquals("newAccessToken", result.get("access"));
        assertEquals("newRefreshToken", result.get("refresh"));
        verify(service).refresh(refreshToken);
    }

    @Test
    void createUserByAdmin_Success() {
        // Arrange
        when(service.createByAdmin(any(UserCreateDto.class), any(RoleType.class)))
            .thenReturn(userDto);

        // Act
        UserDto result = userController.createUserByAdmin(createDto, RoleType.ADMIN);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(service).createByAdmin(createDto, RoleType.ADMIN);
    }

    @Test
    void createUserByAdmin_WithUserRole() {
        // Arrange
        when(service.createByAdmin(any(UserCreateDto.class), any(RoleType.class)))
            .thenReturn(userDto);

        // Act
        UserDto result = userController.createUserByAdmin(createDto, RoleType.USER);

        // Assert
        assertNotNull(result);
        verify(service).createByAdmin(createDto, RoleType.USER);
    }

    @Test
    void patchUser_Success() {
        // Arrange
        when(service.patch(anyLong(), any(UserPatchDto.class))).thenReturn(userDto);

        // Act
        UserDto result = userController.patchUser(1L, patchDto);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(service).patch(1L, patchDto);
    }

    @Test
    void getMe_Success() {
        // Arrange
        when(service.getMe()).thenReturn(userDto);

        // Act
        UserDto result = userController.getMe();

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(service).getMe();
    }

    @Test
    void findAll_Success() {
        // Arrange
        List<UserDto> users = List.of(userDto);
        when(service.findAll()).thenReturn(users);

        // Act
        List<UserDto> result = userController.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        verify(service).findAll();
    }

    @Test
    void findAll_EmptyList() {
        // Arrange
        when(service.findAll()).thenReturn(List.of());

        // Act
        List<UserDto> result = userController.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(service).findAll();
    }

    @Test
    void findAll_MultipleUsers() {
        // Arrange
        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        
        List<UserDto> users = List.of(userDto, user2);
        when(service.findAll()).thenReturn(users);

        // Act
        List<UserDto> result = userController.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(service).findAll();
    }
}
