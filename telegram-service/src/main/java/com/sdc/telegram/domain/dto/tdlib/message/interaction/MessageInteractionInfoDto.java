package com.sdc.telegram.domain.dto.tdlib.message.interaction;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Message interaction info DTO")
public class MessageInteractionInfoDto {
    @Schema(description = "View count")
    private Integer viewCount;

    @Schema(description = "Forward count")
    private Integer forwardCount;
}

