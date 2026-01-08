package com.sdc.main.domain.dto.telegram.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a regular user
 * @since 12.2025
 */
@Data
@NoArgsConstructor
@Schema(description = "User type regular DTO")
public class UserTypeRegularDto extends UserTypeTdlib {
}
