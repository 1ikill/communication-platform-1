package com.sdc.main.domain.dto.telegram.message;

import com.sdc.main.domain.dto.telegram.message.content.MessageContentTdlib;
import com.sdc.main.domain.dto.telegram.message.interaction.MessageForwardInfoDto;
import com.sdc.main.domain.dto.telegram.message.interaction.MessageImportInfoDto;
import com.sdc.main.domain.dto.telegram.message.interaction.MessageInteractionInfoDto;
import com.sdc.main.domain.dto.telegram.message.interaction.MessageReplyToTdlib;
import com.sdc.main.domain.dto.telegram.message.sending.MessageSenderTdlib;
import com.sdc.main.domain.dto.telegram.message.sending.MessageSendingStateTdlib;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Message DTO")
public class MessageTdlibDto {
    @Schema(description = "Message id")
    private Long id;

    @Schema(description = "Sender id")
    private MessageSenderTdlib senderId;

    @Schema(description = "Chat id")
    private Long chatId;

    @Schema(description = "Sending state")
    private MessageSendingStateTdlib sendingState;

    @Schema(description = "Is outgoing")
    private Boolean isOutgoing;

    @Schema(description = "Is read")
    private Boolean isRead;

    @Schema(description = "Is pinned")
    private Boolean isPinned;

    @Schema(description = "Is from offline")
    private Boolean isFromOffline;

    @Schema(description = "Can be saved")
    private Boolean canBeSaved;

    @Schema(description = "Has timestamped media")
    private Boolean hasTimestampedMedia;

    @Schema(description = "Is channel post")
    private Boolean isChannelPost;

    @Schema(description = "Is topic message")
    private Boolean isTopicMessage;

    @Schema(description = "Contains unread mention")
    private Boolean containsUnreadMention;

    @Schema(description = "Date")
    private Integer date;

    @Schema(description = "Edit date")
    private Integer editDate;

    @Schema(description = "Forward info")
    private MessageForwardInfoDto forwardInfo;

    @Schema(description = "Import info")
    private MessageImportInfoDto importInfo;

    @Schema(description = "Interaction info")
    private MessageInteractionInfoDto interactionInfo;

    @Schema(description = "Reply to")
    private MessageReplyToTdlib replyTo;

    @Schema(description = "Message thread id")
    private Long messageThreadId;

    @Schema(description = "Saved messages topic id")
    private Long savedMessagesTopicId;

    @Schema(description = "Self destruct in")
    private Double selfDestructIn;

    @Schema(description = "Auto delete in")
    private Double autoDeleteIn;

    @Schema(description = "Via bot user id")
    private Long viaBotUserId;

    @Schema(description = "Sender business bot user id")
    private Long senderBusinessBotUserId;

    @Schema(description = "Sender boost count")
    private Integer senderBoostCount;

    @Schema(description = "Author signature")
    private String authorSignature;

    @Schema(description = "Media album id")
    private Long mediaAlbumId;

    @Schema(description = "Effect id")
    private Long effectId;

    @Schema(description = "Has sensitive content")
    private Boolean hasSensitiveContent;

    @Schema(description = "Restriction reason")
    private String restrictionReason;

    @Schema(description = "Content")
    private MessageContentTdlib content;

    @Schema(description = "Photo remote id")
    private String photoRemoteId;

    @Schema(description = "Video remote id")
    private String videoRemoteId;

    @Schema(description = "Document remote id")
    private String documentRemoteId;
}