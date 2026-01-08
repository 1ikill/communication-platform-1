package com.sdc.main.domain.dto.gmail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto for message attachment.
 * @since 12.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Attachment DTO")
public class AttachmentDto {
    @Schema(description = "Attachment id")
    private String attachmentId;

    @Schema(description = "Filename")
    private String filename;

    @Schema(description = "Mime type")
    private String mimeType;

    @Schema(description = "Size")
    private Long size;
}
