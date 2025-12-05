package com.sdc.telegram.domain.dto.tdlib.message.sending;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Message sender chat DTO")
public class MessageSenderChatDto extends MessageSenderTdlib {
    @Schema(description = "Chat id")
    private Long chatId;
}
