package com.sdc.main.domain.dto.telegram.message.origin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message origin chat telegram DTO.
 * @since 12.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Message origin chat DTO")
public class MessageOriginChatDto extends MessageOriginTdlib {
    @Schema(description = "Sender chat id")
    private Long senderChatId;

    @Schema(description = "Author signature")
    private String authorSignature;
}