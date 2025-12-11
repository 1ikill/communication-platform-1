package com.sdc.discord.domain.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.sdc.discord.domain.model.DiscordMessageFile;

/**
 * DTO for {@link DiscordMessageFile}
 * @since 12.2025
 */
@Data
@AllArgsConstructor
@Schema(description = "Discord message file DTO")
public class DiscordMessageFileDto {
    @Schema(description = "Id")
    private Long id;

    @Schema(description = "File name")
    private String fileName;

    @Schema(description = "File type")
    private String fileType;

    @Schema(description = "Discord url")
    private String discordUrl;
}
