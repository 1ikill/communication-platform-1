package com.sdc.telegram.domain.dto.tdlib.message.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description ="Message Text DTO")
public class MessageTextDto extends MessageContentTdlib {
    @Schema(description = "Text")
    private FormattedTextDto text;
}
