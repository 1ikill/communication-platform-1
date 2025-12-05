package com.sdc.telegram.domain.dto.tdlib.message.sending;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Message sending state pending DTO")
public class MessageSendingStatePendingDto extends MessageSendingStateTdlib {
    @Schema(description = "Sending id")
    private Integer sendingId;
}
