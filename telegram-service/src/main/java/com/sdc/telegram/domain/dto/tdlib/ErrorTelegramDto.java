package com.sdc.telegram.domain.dto.tdlib;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description ="Telegram error Dto")
public class ErrorTelegramDto {
    @Schema(description = "Code")
    private Integer code;

    @Schema(description = "Message")
    private String message;
}
