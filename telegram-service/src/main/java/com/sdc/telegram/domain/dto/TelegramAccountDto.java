package com.sdc.telegram.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Connected account DTO")
public class TelegramAccountDto {
    @Schema(description = "Account identifier")
    private String accountId;

    @Schema(description = "Account name")
    private String accountName;

    @Schema(description = "Profile photo link")
    private String photoRemoteId;
}