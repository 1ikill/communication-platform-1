package com.sdc.main.domain.dto.telegram.message.interaction;

import com.sdc.main.domain.dto.telegram.message.content.MessageContentTdlib;
import com.sdc.main.domain.dto.telegram.message.content.TextQuoteDto;
import com.sdc.main.domain.dto.telegram.message.origin.MessageOriginTdlib;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message reply to message telegram DTO.
 * @since 12.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Message reply to message DTO")
public class MessageReplyToMessageDto extends MessageReplyToTdlib {
    @Schema(description = "Chat id")
    private Long chatId;

    @Schema(description = "Message id")
    private Long messageId;

    @Schema(description = "Quote")
    private TextQuoteDto quote;

    @Schema(description = "Origin")
    private MessageOriginTdlib origin;

    @Schema(description = "Origin send date")
    private Integer originSendDate;

    @Schema(description = "Content")
    private MessageContentTdlib content;
}
