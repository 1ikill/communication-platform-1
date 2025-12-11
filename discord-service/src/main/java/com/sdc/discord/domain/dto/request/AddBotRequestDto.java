package com.sdc.discord.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Dto for bot addition request.
 * @since 12.2025
 */
@Schema(description = "Add bot request DTO")
@Data
public class AddBotRequestDto {
    @Schema(description = "Access token")
    @NotBlank
    private String token;
}
