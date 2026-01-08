package com.sdc.main.domain.dto.telegram.user.status;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Дто статуса пользователя - был в прошлом месяце
 * @since 01.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "User status last month DTO")
public class UserStatusLastMonthDto extends UserStatusTdlib {
    @Schema(description = "By my privacy settings")
    private Boolean byMyPrivacySettings;
}
