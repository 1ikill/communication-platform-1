package com.sdc.main.domain.dto.telegram.message.sending;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message sending state pending telegram dto.
 * @since 12.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Message sending state pending DTO")
public class MessageSendingStatePendingDto extends MessageSendingStateTdlib {
    @Schema(description = "Sending id")
    private Integer sendingId;
}
