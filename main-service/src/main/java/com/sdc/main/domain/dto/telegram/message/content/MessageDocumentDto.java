package com.sdc.main.domain.dto.telegram.message.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message document telegram DTO.
 * @since 12.2025
 */
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

