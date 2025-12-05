package com.sdc.main.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Broadcast message request DTO.
 * @since 11.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Broadcast message request DTO")
public class BroadcastMessageRequestDto {
    @Schema(description = "Receivers")
    private List<MessageRequestDto> receivers;

    @Schema(description = "Personalize flag")
    private Boolean personalize;
}
