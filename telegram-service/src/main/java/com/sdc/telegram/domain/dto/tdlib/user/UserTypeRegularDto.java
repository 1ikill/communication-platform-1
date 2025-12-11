package com.sdc.telegram.domain.dto.tdlib.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "User type regular DTO")
/**
 * DTO representing a regular user
 * @since 12.2025
 */
public class UserTypeRegularDto extends UserTypeTdlib {
}
