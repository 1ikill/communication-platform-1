package com.sdc.main.domain.dto.telegram.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description ="Chat permissions DTO")
public class ChatPermissionsDto {
    @Schema(description = "Can send basic messages")
    private Boolean canSendBasicMessages;

    @Schema(description = "Can send audios")
    private Boolean canSendAudios;

    @Schema(description = "Can send documents")
    private Boolean canSendDocuments;

    @Schema(description = "Can send photos")
    private Boolean canSendPhotos;

    @Schema(description = "Can send videos")
    private Boolean canSendVideos;

    @Schema(description = "Can send video notes")
    private Boolean canSendVideoNotes;

    @Schema(description = "Can send voice notes")
    private Boolean canSendVoiceNotes;

    @Schema(description = "Can send polls")
    private Boolean canSendPolls;

    @Schema(description = "Can send other messages")
    private Boolean canSendOtherMessages;

    @Schema(description = "Can add link previews")
    private Boolean canAddLinkPreviews;

    @Schema(description = "Can change info")
    private Boolean canChangeInfo;

    @Schema(description = "Can invite users")
    private Boolean canInviteUsers;

    @Schema(description = "Can pin messages")
    private Boolean canPinMessages;

    @Schema(description = "Can create topics")
    private Boolean canCreateTopics;
}
