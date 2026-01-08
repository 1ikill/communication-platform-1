package com.sdc.main.domain.dto.telegram.message.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Remote File DTO")
public class RemoteFileDto {
    @Schema(description = "Id")
    private String id;

    @Schema(description = "Unique id")
    private String uniqueId;

    @Schema(description = "Is uploading active")
    private Boolean isUploadingActive;

    @Schema(description = "Is uploading completed")
    private Boolean isUploadingCompleted;

    @Schema(description = "Uploaded size")
    private Long uploadedSize;
}
