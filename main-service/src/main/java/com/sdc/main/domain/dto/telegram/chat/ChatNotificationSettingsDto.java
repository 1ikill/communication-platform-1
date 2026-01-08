package com.sdc.main.domain.dto.telegram.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description ="Chat notification setting DTO")
public class ChatNotificationSettingsDto {
    @Schema(description = "Use default mute for")
    private Boolean useDefaultMuteFor;

    @Schema(description = "Mute for period")
    private Integer muteFor;

    @Schema(description = "Use default sound")
    private Boolean useDefaultSound;

    @Schema(description = "Sound id")
    private Long soundId;

    @Schema(description = "Use default show preview")
    private Boolean useDefaultShowPreview;

    @Schema(description = "Show preview")
    private Boolean showPreview;

    @Schema(description = "Use default mute stories")
    private Boolean useDefaultMuteStories;

    @Schema(description = "Mute stories")
    private Boolean muteStories;

    @Schema(description = "Use default story sound")
    private Boolean useDefaultStorySound;

    @Schema(description = "Story sound id")
    private Long storySoundId;

    @Schema(description = "Use default show story sender")
    private Boolean useDefaultShowStorySender;

    @Schema(description = "Show story sender")
    private Boolean showStorySender;

    @Schema(description = "Use default disable pinned message notifications")
    private Boolean useDefaultDisablePinnedMessageNotifications;

    @Schema(description = "Disable pinned message notifications")
    private Boolean disablePinnedMessageNotifications;

    @Schema(description = "Use default disable mention notifications")
    private Boolean useDefaultDisableMentionNotifications;

    @Schema(description = "Disable mention notifications")
    private Boolean disableMentionNotifications;
}
