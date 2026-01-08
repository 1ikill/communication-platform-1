package com.sdc.main.domain.dto.telegram.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a bot user
 * @since 12.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "User type bot DTO")
public class UserTypeBotDto extends UserTypeTdlib {
    @Schema(description = "Can be edited")
    private Boolean canBeEdited;

    @Schema(description = "Can join groups")
    private Boolean canJoinGroups;

    @Schema(description = "Can read all group messages")
    private Boolean canReadAllGroupMessages;

    @Schema(description = "Has main webApp")
    private Boolean hasMainWebApp;

    @Schema(description = "Is inline")
    private Boolean isInline;

    @Schema(description = "Inline query placeholder")
    private String inlineQueryPlaceholder;

    @Schema(description = "Need location")
    private Boolean needLocation;

    @Schema(description = "Can connect to business")
    private Boolean canConnectToBusiness;

    @Schema(description = "Can be added to attachment menu")
    private Boolean canBeAddedToAttachmentMenu;

    @Schema(description = "Active user count")
    private Integer activeUserCount;
}
