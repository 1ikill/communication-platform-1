package com.sdc.main.domain.dto.telegram.user.status;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Дто статуса пользователя - был на прошлой неделе
 * @since 01.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "User status last week DTO")
public class UserStatusLastWeekDto extends UserStatusTdlib {
    @Schema(description = "By my privacy settings")
    private Boolean byMyPrivacySettings;
}
