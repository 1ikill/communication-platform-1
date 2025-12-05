package com.sdc.telegram.domain.dto.tdlib.message.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Alternative video DTO")
public class AlternativeVideoDto {
    @Schema(description = "Width")
    private Integer width;

    @Schema(description = "Height")
    private Integer height;

    @Schema(description = "Codec")
    private String codec;

    @Schema(description = "Hls file")
    private FileDto hlsFile;

    @Schema(description = "Video")
    private FileDto video;
}
