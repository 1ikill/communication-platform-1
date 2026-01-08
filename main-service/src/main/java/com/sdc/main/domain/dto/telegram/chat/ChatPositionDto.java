package com.sdc.main.domain.dto.telegram.chat;

import com.sdc.main.domain.dto.telegram.chat.list.ChatListTdlib;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description ="Chat position DTO")
public class ChatPositionDto {
    @Schema(description = "Chat list")
    private ChatListTdlib list;

    @Schema(description = "Chat order")
    private Long order;

    @Schema(description = "Is pinned")
    private Boolean isPinned;
}
