package com.sdc.telegram.domain.dto.tdlib.message.interaction;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Message import info DTO")
public class MessageImportInfoDto {
    @Schema(description = "Sender name")
    private String senderName;

    @Schema(description = "Date")
    private Integer date;
}
