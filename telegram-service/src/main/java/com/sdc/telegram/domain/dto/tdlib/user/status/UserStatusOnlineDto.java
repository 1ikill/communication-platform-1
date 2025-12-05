package com.sdc.telegram.domain.dto.tdlib.user.status;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Дто статуса онлайн
 * @since 01.2025
 */
@Data
@AllArgsConstructor
@Schema(description ="User status online DTO")
public class UserStatusOnlineDto extends UserStatusTdlib {
    @Schema(description = "Expires")
    private Integer expires;
}
