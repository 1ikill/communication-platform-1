package com.sdc.user.service;

import com.sdc.user.config.security.JwtTokenProvider;
import com.sdc.user.domain.constants.RoleType;
import com.sdc.user.domain.dto.AuthRequestDto;
import com.sdc.user.domain.dto.UserCreateDto;
import com.sdc.user.domain.dto.UserDto;
import com.sdc.user.domain.dto.UserPatchDto;
import com.sdc.user.domain.mapper.UserMapper;
import com.sdc.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.sdc.user.domain.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.sdc.user.domain.constants.RoleType.ADMIN;
import static com.sdc.user.domain.constants.RoleType.USER;
import static java.util.Objects.nonNull;

/**
 * Service for {@link User}
 * @since 10.2025
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthService authService;

    /**
     * Register user.
     * @param createDto dto to create user.
     * @return UserDto with user data.
     */
    public UserDto register(final UserCreateDto createDto) {
        final User user = mapper.fromCreateDto(createDto);
        if (userRepository.existsByEmail(user.getEmail()) || userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("User with that email or username already exists.");
        }

        user.setRole(USER);
        user.setPasswordHash(passwordEncoder.encode(createDto.getPassword()));

        return mapper.toDto(userRepository.save(user));
    }

    /**
     * Create user.
     * @param createDto dto to create user.
     * @param role created user role.
     * @return UserDto with user data.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto createByAdmin(final UserCreateDto createDto, final RoleType role) {
        final User user = mapper.fromCreateDto(createDto);
        if (userRepository.existsByEmail(user.getEmail()) || userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("User already exists.");
        }

        user.setRole(role);
        user.setPasswordHash(passwordEncoder.encode(createDto.getPassword()));

        return mapper.toDto(userRepository.save(user));
    }

    /**
     * Login
     * @param authRequestDto dto with auth request data.
     * @return Map with access and refresh tokens.
     */
    public Map<String, String> login(final AuthRequestDto authRequestDto) {
        final User user = userRepository.findByUsername(authRequestDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(authRequestDto.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        final String access = tokenProvider.generateAccessToken(user);
        final String refresh = tokenProvider.generateRefreshToken(user);

        final Map<String, String> tokens = new HashMap<>();
        tokens.put("access", access);
        tokens.put("refresh", refresh);

        return tokens;
    }

    /**
     * Refresh token
     * @param refreshToken refresh token.
     * @return Map with access and refresh tokens.
     */
    public Map<String, String> refresh(final String refreshToken) {
        final Long userId = tokenProvider.parseRefreshToken(refreshToken);

        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        final String newAccess = tokenProvider.generateAccessToken(user);
        final String newRefresh = tokenProvider.generateRefreshToken(user);

        final Map<String, String> tokens = new HashMap<>();
        tokens.put("access", newAccess);
        tokens.put("refresh", newRefresh);

        return tokens;
    }

    /**
     * Find all users.
     * @return UserDto list with all users.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Patch user.
     * @param id target user id.
     * @param patchDto dto with patch data.
     * @return UserDto with user data.
     */
    @Transactional
    public UserDto patch(final Long id, final UserPatchDto patchDto) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

        final UserPatchDto beforeUpdate = mapper.toPatchDto(user);
        final UserPatchDto afterUpdate = mapper.toPatchDto(user);
        mapper.mergeDto(afterUpdate, patchDto);

        validateChanges(beforeUpdate, afterUpdate);

        mapper.merge(user, afterUpdate);
        if (nonNull(patchDto.getPassword())) {
            user.setPasswordHash(passwordEncoder.encode(patchDto.getPassword()));
        }
        return mapper.toDto(user);
    }

    /**
     * Validate patch changes
     * @param beforeUpdate entity before update.
     * @param afterUpdate entity after update.
     */
    private void validateChanges(final UserPatchDto beforeUpdate, final UserPatchDto afterUpdate) {
        if (!Objects.equals(beforeUpdate.getEmail(), afterUpdate.getEmail())) {
            if (userRepository.existsByEmail(afterUpdate.getEmail())) {
                throw new IllegalArgumentException("Email already in use.");
            }
        }
        if (!Objects.equals(beforeUpdate.getUsername(), afterUpdate.getUsername())) {
            if (userRepository.existsByUsername(afterUpdate.getUsername())) {
                throw new IllegalArgumentException("Username already in use.");
            }
        }
        if (!Objects.equals(beforeUpdate.getRole(), afterUpdate.getRole())) {
            if (!Objects.equals(authService.getCurrentUserRole(), ADMIN)) {
                afterUpdate.setRole(beforeUpdate.getRole());
            }
        }
    }

    /**
     * Get user self data.
     * @return UserDto with user data.
     */
    public UserDto getMe() {
        final User user = userRepository.getById(authService.getCurrentUserId());
        return mapper.toDto(user);
    }
}
