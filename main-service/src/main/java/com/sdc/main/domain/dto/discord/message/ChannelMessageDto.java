package com.sdc.main.domain.dto.discord.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO with channel message info.
 * @since 12.2025
 */
@Data
@AllArgsConstructor
@Schema(description = "Channel message DTO")
public class ChannelMessageDto {
    @Schema(description = "Id")
    private String id;

    @Schema(description = "Author name")
    private String authorName;

    @Schema(description = "Author id")
    private String authorId;

    @Schema(description = "Content")
    private String content;

    @Schema(description = "Timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime timestamp;

    @Schema(description = "Is bot")
    private boolean bot;

    @Schema(description = "Is self")
    private boolean self;

    @Schema(description = "Attachments")
    private List<DiscordMessageFileDto> attachments;
}