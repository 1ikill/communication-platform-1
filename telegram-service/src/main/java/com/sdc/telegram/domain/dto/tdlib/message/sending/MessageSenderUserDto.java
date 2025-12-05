package com.sdc.telegram.domain.dto.tdlib.message.sending;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Message sender user DTO")
public class MessageSenderUserDto extends MessageSenderTdlib {
    @Schema(description = "User id")
    private Long userId;
}
