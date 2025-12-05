package com.sdc.telegram.domain.dto.tdlib.user.status;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Дто статуса офлайн
 * @since 01.2025
 */
@Data
@AllArgsConstructor
@Schema(description = "User status offline DTO")
public class UserStatusOfflineDto extends UserStatusTdlib {
    @Schema(description = "Was online")
    private Integer wasOnline;
}
