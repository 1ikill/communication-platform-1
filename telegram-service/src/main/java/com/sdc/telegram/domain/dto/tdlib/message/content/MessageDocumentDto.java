package com.sdc.telegram.domain.dto.tdlib.message.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description ="Message Document DTO")
public class MessageDocumentDto extends MessageContentTdlib {
    @Schema(description = "Document")
    private DocumentDto document;

    @Schema(description = "Caption")
    private FormattedTextDto caption;
}

