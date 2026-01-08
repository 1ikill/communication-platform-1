package com.sdc.main.domain.dto.telegram.chat.type;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Chat type basic group telegram DTO.
 * @since 12.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Group chat DTO")
public class ChatTypeBasicGroupDto extends ChatTypeTdlib {
    @Schema(description = "Group id")
    private Long basicGroupId;
}
