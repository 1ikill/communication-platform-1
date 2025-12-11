package com.sdc.discord.domain.dto.bot;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for displaying connected bot info
 * @since 12.2025
 */
@Data
@AllArgsConstructor
@Schema(description = "Discord bot info DTO")
public class DiscordBotInfoDto {
    @Schema(description = "Id")
    private Long id;

    @Schema(description = "User id")
    private Long userId;

    @Schema(description = "Bot user id")
    private String botUserId;

    @Schema(description = "Bot username")
    private String botUsername;

    @Schema(description = "Is active")
    private Boolean isActive;

    @Schema(description = "Created date")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdDate;

    @Schema(description = "Last modified date")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime lastModifiedDate;
}
