package com.sdc.main.domain.dto.telegram.message.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description ="File DTO")
public class FileDto {
    @Schema(description = "File id")
    private Integer id;

    @Schema(description = "Size")
    private Long size;

    @Schema(description = "Expected Size")
    private Long expectedSize;

    @Schema(description = "Local file")
    private LocalFileDto local;

    @Schema(description = "Remote file")
    private RemoteFileDto remote;
}
