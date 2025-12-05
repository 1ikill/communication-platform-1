package com.sdc.telegram.domain.dto.tdlib.message.origin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
