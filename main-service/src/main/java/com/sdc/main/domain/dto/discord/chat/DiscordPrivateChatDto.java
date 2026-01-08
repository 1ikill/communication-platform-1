package com.sdc.main.domain.dto.discord.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for DiscordPrivateChat.
 * @since 12.2025
 */
@Data
@AllArgsConstructor
@Schema(description = "Discord private chat DTO")
public class DiscordPrivateChatDto {
    @Schema(description = "Id")
    private Long id;

    @Schema(description = "Bot id")
    private Long botId;

    @Schema(description = "Channel id")
    private String channelId;

    @Schema(description = "User id")
    private String userId;

    @Schema(description = "User name")
    private String userName;

    @Schema(description = "User avatar url")
    private String userAvatarUrl;

    @Schema(description = "Last message time")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime lastMessageTime;

    @Schema(description = "Last message id")
    private String lastMessageId;

    @Schema(description = "Message count")
    private Integer messageCount;

    @Schema(description = "Created date")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdDate;

    @Schema(description = "Last modified date")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime lastModifiedDate;
}
