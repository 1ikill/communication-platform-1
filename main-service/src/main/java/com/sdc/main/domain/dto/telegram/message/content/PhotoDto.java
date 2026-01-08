package com.sdc.main.domain.dto.telegram.message.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Photo DTO")
public class PhotoDto {
    @Schema(description = "Has stickers")
    private Boolean hasStickers;

    @Schema(description = "Minithumbnail")
    private MinithumbnailDto minithumbnail;

    @Schema(description = "Sizes")
    private List<PhotoSizeDto> sizes;
}

