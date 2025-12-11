package com.sdc.gmail.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Gmail account info DTO
 * @since 12.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Gmail account info DTO")
public class GmailAccountInfoDto {
    @Schema(description = "Account id")
    private Long accountId;

    @Schema(description = "Gmail")
    private String gmail;
}
