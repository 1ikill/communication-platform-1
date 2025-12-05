package com.sdc.telegram.domain.dto.tdlib.chat.type;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
