package com.sdc.main.domain.dto.telegram.message.origin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message origin channel telegram DTO.
 * @since 12.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Message origin chanel DTO")
public class MessageOriginChanelDto extends MessageOriginTdlib {
    @Schema(description = "Chat id")
    private Long chatId;

    @Schema(description = "Message id")
    private Long messageId;

    @Schema(description = "Author signature")
    private String authorSignature;
}
