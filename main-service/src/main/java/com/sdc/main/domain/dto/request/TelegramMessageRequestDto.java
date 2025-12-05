package com.sdc.main.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Telegram message request DTO.
 * @since 11.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Telegram message request DTO")
public class TelegramMessageRequestDto extends MessageRequestDto{
    @Schema(description = "Account id")
    private String accountId;
}
