package com.sdc.telegram.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO representing Telegram chat notification with unread count
 * @since 12.2025
 */
@Data
@AllArgsConstructor
@Schema(description = "Telegram chat notification DTO")
public class TelegramNotificationDto {
    @Schema(description = "Account identifier")
    private String accountId;

    @Schema(description = "Chat id")
    private Long chatId;

    @Schema(description = "Notification count")
    private Integer notificationsCount;
}
