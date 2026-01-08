package com.sdc.main.domain.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for authentication
 * @since 10.2025
 */
@Data
@Schema(description = "Dto for authentication")
public class AuthRequestDto {
    @Schema(description = "Username")
    @NotBlank(message = "User name must not be blank")
    private String username;

    @Schema(description = "Password")
    @NotBlank(message = "Password must not be blank")
    private String password;
}
