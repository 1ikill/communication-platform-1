package com.sdc.telegram.domain.dto.tdlib.user.status;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Дто статуса пользователя - был на прошлой неделе
 * @since 01.2025
 */
@Data
@AllArgsConstructor
@Schema(description = "User status last week DTO")
public class UserStatusLastWeekDto extends UserStatusTdlib {
    @Schema(description = "By my privacy settings")
    private Boolean byMyPrivacySettings;
}
