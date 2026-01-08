package com.sdc.main.domain.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdc.main.domain.constants.user.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for User.
 * @since 10.2025
 */
@Data
@Schema(description = "Dto for user")
public class UserDto {
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "Username")
    private String username;

    @Schema(description = "Full name")
    private String fullName;

    @Schema(description = "Role")
    private RoleType role;

    @Schema(description = "Created date")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    protected LocalDateTime createdDate;

    @Schema(description = "Last modified date")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    protected LocalDateTime lastModifiedDate;
}
