package com.sdc.discord.domain.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.sdc.discord.domain.model.DiscordPrivateMessage;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link DiscordPrivateMessage}
 * @since 12.2025
 */
@Data
@AllArgsConstructor
@Schema(description = "Discord private message DTO")
public class DiscordPrivateMessageDto {
    @Schema(description = "Id")
    private Long id;

    @Schema(description = "Bot id")
    private Long botId;

    @Schema(description = "Discord message id")
    private String discordMessageId;

    @Schema(description = "Channel id")
    private String channelId;

    @Schema(description = "Author id")
    private String authorId;

    @Schema(description = "Author name")
    private String authorName;

    @Schema(description = "Content")
    private String content;

    @Schema(description = "Is from bot")
    private Boolean isFromBot;

    @Schema(description = "Has attachments")
    private boolean hasAttachments;

    @Schema(description = "Files")
    private List<DiscordMessageFileDto> files;

    @Schema(description = "Timestamp")
    private LocalDateTime timestamp;

    @Schema(description = "Created date")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdDate;

    @Schema(description = "Last modified date")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime lastModifiedDate;
}