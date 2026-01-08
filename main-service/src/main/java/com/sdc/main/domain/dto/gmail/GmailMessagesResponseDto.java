package com.sdc.main.domain.dto.gmail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Dto for gmail messages response.
 * @since 12.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Gmail messages response DTO")
public class GmailMessagesResponseDto {
    @Schema(description = "Messages")
    private List<GmailMessageDto> messages;

    @Schema(description = "Next page token")
    private String nextPageToken;

    @Schema(description = "Result size estimate")
    private Long resultSizeEstimate;
}