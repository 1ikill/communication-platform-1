package com.sdc.main.domain.dto.telegram.message.content;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description ="Formatted Text DTO")
public class FormattedTextDto {
    @Schema(description = "Text")
    public String text;
}