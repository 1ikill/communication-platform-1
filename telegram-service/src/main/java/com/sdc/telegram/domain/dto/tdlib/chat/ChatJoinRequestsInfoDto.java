package com.sdc.telegram.domain.dto.tdlib.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Chat join requests info DTO")
public class ChatJoinRequestsInfoDto {
    @Schema(description = "Requests count")
    private Integer totalCount;

    @Schema(description = "User identifiers")
    private List<Long> userIds;
}

