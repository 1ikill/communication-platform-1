package com.sdc.telegram.domain.dto.tdlib.message.interaction;

import com.sdc.telegram.domain.dto.tdlib.message.content.MessageContentTdlib;
import com.sdc.telegram.domain.dto.tdlib.message.content.TextQuoteDto;
import com.sdc.telegram.domain.dto.tdlib.message.origin.MessageOriginTdlib;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
