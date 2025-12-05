package com.sdc.main.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Gmail message request DTO.
 * @since 11.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Gmail message request DTO")
public class GmailMessageRequestDto extends MessageRequestDto {
    @Schema(description = "Account id")
    private Long accountId;

    @Schema(description = "Subject")
    private String subject;
}
