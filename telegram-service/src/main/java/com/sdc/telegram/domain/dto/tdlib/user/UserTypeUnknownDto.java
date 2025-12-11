package com.sdc.telegram.domain.dto.tdlib.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "User type unknown DTO")
/**
 * DTO representing an unknown user type
 * @since 12.2025
 */
public class UserTypeUnknownDto extends UserTypeTdlib {
}
