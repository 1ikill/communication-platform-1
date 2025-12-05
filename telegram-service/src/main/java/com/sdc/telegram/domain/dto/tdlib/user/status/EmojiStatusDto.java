package com.sdc.telegram.domain.dto.tdlib.user.status;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Дто эмодзи статуса
 * @since 01.2025
 */
@Data
@Schema(description = "Emoji status DTO")
public class EmojiStatusDto {
    @Schema(description = "Custom emoji id")
    private Long customEmojiId;

    @Schema(description = "Expiration date")
    private Integer expirationDate;
}
