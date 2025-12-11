package com.sdc.discord.domain.dto.guild;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO with guild channel info.
 * @since 12.2025
 */
@Data
@AllArgsConstructor
@Schema(description = "Guild channel DTO")
public class GuildChannelDto {
    @Schema(description = "Id")
    private String id;

    @Schema(description = "Name")
    private String name;

    @Schema(description = "Topic")
    private String topic;

    @Schema(description = "Position")
    private int position;
}
