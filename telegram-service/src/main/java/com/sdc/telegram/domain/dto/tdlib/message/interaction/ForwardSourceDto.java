package com.sdc.telegram.domain.dto.tdlib.message.interaction;

import com.sdc.telegram.domain.dto.tdlib.message.sending.MessageSenderTdlib;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Forward source DTO")
public class ForwardSourceDto {
    @Schema(description = "Chat id")
    private Long chatId;

    @Schema(description = "Message id")
    private Long messageId;

    @Schema(description = "Sender id")
    private MessageSenderTdlib senderId;

    @Schema(description = "Sender name")
    private String senderName;

    @Schema(description = "Date")
    private Integer date;

    @Schema(description = "Is outgoing")
    private Boolean isOutgoing;
}
