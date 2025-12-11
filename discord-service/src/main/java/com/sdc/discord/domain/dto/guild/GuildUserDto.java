package com.sdc.discord.domain.dto.guild;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO with guild user info.
 * @since 12.2025
 */
@Data
@AllArgsConstructor
@Schema(description = "Guild user DTO")
public class GuildUserDto {
    @Schema(description = "Id")
    private String id;

    @Schema(description = "Username")
    private String username;

    @Schema(description = "Global name")
    private String globalName;

    @Schema(description = "Avatar url")
    private String avatarUrl;

    @Schema(description = "Guild name")
    private String guildName;

    @Schema(description = "Is bot")
    private boolean isBot;
}
