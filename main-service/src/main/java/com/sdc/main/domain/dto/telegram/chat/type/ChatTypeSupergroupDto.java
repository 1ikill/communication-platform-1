package com.sdc.main.domain.dto.telegram.chat.type;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Chat type private telegram DTO.
 * @since 12.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Supergroup chat DTO")
public class ChatTypeSupergroupDto extends ChatTypeTdlib {
    @Schema(description = "Supergroup id")
    private Long supergroupId;

    @Schema(description = "Is channel")
    private Boolean isChannel;
}
