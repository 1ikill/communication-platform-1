package com.sdc.main.domain.dto.telegram.message.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description ="Local File DTO")
public class LocalFileDto {
    @Schema(description = "Path")
    private String path;

    @Schema(description = "Can be downloaded")
    private Boolean canBeDownloaded;

    @Schema(description = "Can be deleted")
    private Boolean canBeDeleted;

    @Schema(description = "Is downloading active")
    private Boolean isDownloadingActive;

    @Schema(description = "Is downloading completed")
    private Boolean isDownloadingCompleted;

    @Schema(description = "Download offset")
    private Long downloadOffset;

    @Schema(description = "Downloaded prefix size")
    private Long downloadedPrefixSize;

    @Schema(description = "Downloaded size")
    private Long downloadedSize;
}
