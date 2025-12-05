package com.sdc.telegram.domain.dto.tdlib.message.origin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Message origin hidden user DTO")
public class MessageOriginHiddenUserDto extends MessageOriginTdlib {
    @Schema(description = "Sender name")
    private String senderName;
}
