package com.sdc.main.domain.dto.telegram.message.origin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message origin hidden user telegram DTO.
 * @since 12.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Message origin hidden user DTO")
public class MessageOriginHiddenUserDto extends MessageOriginTdlib {
    @Schema(description = "Sender name")
    private String senderName;
}
