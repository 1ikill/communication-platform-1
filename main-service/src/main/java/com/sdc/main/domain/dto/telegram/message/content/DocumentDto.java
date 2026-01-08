package com.sdc.main.domain.dto.telegram.message.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Document DTO")
public class DocumentDto {
    @Schema(description = "File name")
    private String fileName;

    @Schema(description = "Mime type")
    private String mimeType;

    @Schema(description = "Minithumbnail")
    private MinithumbnailDto minithumbnail;

    @Schema(description = "Document")
    private FileDto document;
}
