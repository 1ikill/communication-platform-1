package com.sdc.main.domain.dto.telegram.message.origin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message origin user telegram DTO.
 * @since 12.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Message origin user DTO")
public class MessageOriginUserDto extends MessageOriginTdlib {
    @Schema(description = "Sender user id")
    private Long senderUserId;
}

