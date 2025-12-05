package com.sdc.telegram.domain.dto.tdlib.message.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Video DTO")
public class VideoDto {
    @Schema(description = "Duration")
    private Integer duration;

    @Schema(description = "Width")
    private Integer width;

    @Schema(description = "Height")
    private Integer height;

    @Schema(description = "File name")
    private String fileName;

    @Schema(description = "Mime type")
    private String mimeType;

    @Schema(description = "Has stickers")
    private Boolean hasStickers;

    @Schema(description = "Supports streaming")
    private Boolean supportsStreaming;

    @Schema(description = "Minithumbnail")
    private MinithumbnailDto minithumbnail;

    @Schema(description = "Video")
    private FileDto video;

}
