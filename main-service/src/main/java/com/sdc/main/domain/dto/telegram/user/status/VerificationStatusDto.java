package com.sdc.main.domain.dto.telegram.user.status;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Дто статуса верификации пользователя
 * @since 01.2025
 */
@Data
@AllArgsConstructor
@Schema(description = "Verification status DTO")
public class VerificationStatusDto {
    @Schema(description = "Is verified")
    private Boolean isVerified;

    @Schema(description = "Is scam")
    private Boolean isScam;

    @Schema(description = "Is fake")
    private Boolean isFake;

    @Schema(description = "Bot verification icon custom emoji id")
    private Long botVerificationIconCustomEmojiId;
}
