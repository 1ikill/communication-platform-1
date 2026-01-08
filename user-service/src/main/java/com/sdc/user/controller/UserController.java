package com.sdc.user.controller;

import com.sdc.user.domain.constants.RoleType;
import com.sdc.user.domain.dto.AuthRequestDto;
import com.sdc.user.domain.dto.UserCreateDto;
import com.sdc.user.domain.dto.UserDto;
import com.sdc.user.domain.dto.UserPatchDto;
import com.sdc.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.sdc.user.domain.model.User;

import java.util.List;
import java.util.Map;

/**
 * Controller to work with {@link User}
 * @since 10.2025
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @Operation(summary = "Register")
    @PostMapping("/auth/register")
    public UserDto register(
            @RequestBody
            @Valid
            final UserCreateDto createDto
    ) {
        log.info("Received request POST /users/auth/register");
        final UserDto result = service.register(createDto);
        log.info("Produced response 200 for POST /users/auth/register with body:{}", result);
        return result;
    }

    @Operation(summary = "Login")
    @PostMapping("/auth/login")
    public Map<String, String> login(
            @RequestBody
            @Valid
            final AuthRequestDto request
            ) {
        log.info("Received request POST /users/auth/login");
        final Map<String, String> result = service.login(request);
        log.info("Produced response 200 for POST /users/auth/login");
        return result;
    }

    @Operation(summary = "Refresh token")
    @PostMapping("/auth/refresh")
    public Map<String, String> refresh(
            @RequestParam
            String refreshToken
    ) {
        log.info("Received request POST /users/auth/refresh");
        final Map<String, String> result = service.refresh(refreshToken);
        log.info("Produced response 200 for POST /users/auth/refresh");
        return result;
    }

    @Operation(summary = "Create user")
    @PostMapping("/admin/create-user")
    public UserDto createUserByAdmin(
            @RequestBody
            @Valid
            final UserCreateDto createDto,
            @RequestParam
            final RoleType role
            ) {
        log.info("Received request POST /users/admin/create-user");
        final UserDto result = service.createByAdmin(createDto, role);
        log.info("Produced response 200 for POST /users/admin/create-user with body:{}", result);
        return result;
    }

    @Operation(summary = "Patch user")
    @PatchMapping("/{id}")
    public UserDto patchUser(
            @PathVariable
            final Long id,
            @RequestBody
            @Valid
            final UserPatchDto patchDto
    ) {
        log.info("Received request PATCH /users/{}", id);
        final UserDto result = service.patch(id, patchDto);
        log.info("Produced response 200 for PATCH /users/{} with body:{}", id, result);
        return result;
    }

    @Operation(summary = "Get self iformation")
    @GetMapping("/me")
    public UserDto getMe() {
        log.info("Received request GET users/me");
        final UserDto result = service.getMe();
        log.info("Produced response 200 for GET users/me with body:{}", result);
        return result;
    }

    @Operation(summary = "Get all users")
    @GetMapping
    public List<UserDto> findAll() {
        return service.findAll();
    }

}
