package com.sdc.main.domain.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Dto for creation of User.
 */
@Data
@Schema(description = "Dto for creation of User")
public class UserCreateDto {
    @Schema(description = "Email")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Username")
    @NotBlank(message = "Username is required")
    @Size(min = 5, max = 25, message = "Username must be between 5 and 25 characters")
    private String username;

    @Schema(description = "FullName")
    @NotBlank(message = "Full name is required")
    @Size(min = 5, max = 100, message = "Full name must be between 5 and 100 characters")
    private String fullName;

    @Schema(description = "Password")
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be 8–128 characters long")
    private String password;
}
