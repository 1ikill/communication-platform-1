package com.sdc.telegram.domain.dto.tdlib.message.content;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Text Quote DTO")
public class TextQuoteDto {
    @Schema(description = "Text")
    private FormattedTextDto text;

    @Schema(description = "Position")
    private Integer position;

    @Schema(description = "Is manual")
    private Boolean isManual;
}
