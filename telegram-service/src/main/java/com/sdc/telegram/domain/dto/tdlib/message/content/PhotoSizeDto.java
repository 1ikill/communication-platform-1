package com.sdc.telegram.domain.dto.tdlib.message.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Photo size DTO")
public class PhotoSizeDto {
    @Schema(description = "Type")
    private String type;

    @Schema(description = "Photo")
    private FileDto photo;

    @Schema(description = "Width")
    private Integer width;

    @Schema(description = "Height")
    private Integer height;

    @Schema(description = "Progressive sizes")
    private List<Integer> progressiveSizes;
}
