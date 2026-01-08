package com.sdc.main.domain.dto.telegram.message.sending;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message sender chat telegram DTO.
 * @since 12.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Message sender chat DTO")
public class MessageSenderChatDto extends MessageSenderTdlib {
    @Schema(description = "Chat id")
    private Long chatId;
}
