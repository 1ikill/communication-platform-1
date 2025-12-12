package com.sdc.main.domain.dto.request;

import com.sdc.main.domain.constants.DiscordMessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Discord message request dto
 * @since 12.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Discord message request DTO")
public class DiscordMessageRequestDto  extends MessageRequestDto {
    @Schema(description = "Account id")
    private Long accountId;

    @Schema(description = "Message type")
    private DiscordMessageType messageType;
}
