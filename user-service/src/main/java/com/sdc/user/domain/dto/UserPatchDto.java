package com.sdc.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sdc.user.domain.constants.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.sdc.user.domain.model.User;

/**
 * Dto for patching {@link User}.
 * @since 11.2025
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Dto for patching user")
public class UserPatchDto {
    @Schema(description = "Email")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Username")
    @Size(min = 5, max = 25, message = "Username must be between 5 and 25 characters")
    private String username;

    @Schema(description = "Full name")
    @Size(min = 5, max = 100, message = "Full name must be between 5 and 100 characters")
    private String fullName;

    @Schema(description = "Password")
    @Size(min = 8, max = 128, message = "Password must be 8–128 characters long")
    private String password;

    @Schema(description = "Role")
    private RoleType role;
}
