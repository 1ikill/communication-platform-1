package com.sdc.main.domain.dto.telegram.message.sending;

import com.sdc.main.domain.dto.telegram.ErrorTelegramDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message sending state failed telegram DTO.
 * @since 12.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Message sending state failed DTO")
public class MessageSendingStateFailedDto extends MessageSendingStateTdlib {
    @Schema(description = "Error")
    private ErrorTelegramDto error;

    @Schema(description = "Can retry")
    private Boolean canRetry;

    @Schema(description = "Need another sender")
    private Boolean needAnotherSender;

    @Schema(description = "Need another reply quote")
    private Boolean needAnotherReplyQuote;

    @Schema(description = "Need drop reply")
    private Boolean needDropReply;

    @Schema(description = "Retry after")
    private Double retryAfter;
}
