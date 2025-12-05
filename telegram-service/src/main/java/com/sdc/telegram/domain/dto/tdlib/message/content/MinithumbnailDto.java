package com.sdc.telegram.domain.dto.tdlib.message.content;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Minithumbnail dto")
public class MinithumbnailDto {
    @Schema(description = "Width")
    private Integer width;

    @Schema(description = "Height")
    private Integer height;

    @Schema(description = "Data")
    private byte[] data;
}
