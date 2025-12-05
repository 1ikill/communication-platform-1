package com.sdc.telegram.domain.dto.tdlib.message.interaction;

import com.sdc.telegram.domain.dto.tdlib.message.origin.MessageOriginTdlib;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Message forward info DTO")
public class MessageForwardInfoDto {
    @Schema(description = "Origin")
    private MessageOriginTdlib origin;

    @Schema(description = "Date")
    private Integer date;

    @Schema(description = "Source")
    private ForwardSourceDto source;

    @Schema(description = "Public service announcement type")
    private String publicServiceAnnouncementType;
}
