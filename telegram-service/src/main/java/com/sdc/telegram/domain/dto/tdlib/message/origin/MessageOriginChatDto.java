package com.sdc.telegram.domain.dto.tdlib.message.origin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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