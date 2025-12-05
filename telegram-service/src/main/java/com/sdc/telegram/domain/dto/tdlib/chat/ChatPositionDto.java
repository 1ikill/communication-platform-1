package com.sdc.telegram.domain.dto.tdlib.chat;

import com.sdc.telegram.domain.dto.tdlib.chat.list.ChatListTdlib;
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
