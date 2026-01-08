package com.sdc.main.domain.dto.telegram.message.sending;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message sender user telegram DTO.
 * @since 12.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Message sender user DTO")
public class MessageSenderUserDto extends MessageSenderTdlib {
    @Schema(description = "User id")
    private Long userId;
}
