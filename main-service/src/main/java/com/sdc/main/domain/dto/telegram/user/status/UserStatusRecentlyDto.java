package com.sdc.main.domain.dto.telegram.user.status;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Дто статуса - был недавно
 * @since 01.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "User status recently DTO")
public class UserStatusRecentlyDto extends UserStatusTdlib {
    @Schema(description = "By my privacy settings")
    private Boolean byMyPrivacySettings;
}
