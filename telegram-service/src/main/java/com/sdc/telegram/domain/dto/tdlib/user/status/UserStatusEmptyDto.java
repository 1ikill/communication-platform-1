package com.sdc.telegram.domain.dto.tdlib.user.status;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Дто пустого статуса пользователя
 * @since 01.2025
 */
@Data
@NoArgsConstructor
@Schema(description = "User status empty DTO")
public class UserStatusEmptyDto extends UserStatusTdlib {
}
