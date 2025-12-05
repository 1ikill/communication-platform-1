package com.sdc.telegram.domain.dto.tdlib.user;

import com.sdc.telegram.domain.dto.tdlib.user.status.EmojiStatusDto;
import com.sdc.telegram.domain.dto.tdlib.user.status.UserStatusTdlib;
import com.sdc.telegram.domain.dto.tdlib.user.status.VerificationStatusDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "User tdlib DTO")
public class UserTdlibDto {
    @Schema(description = "Id")
    private Long id;

    @Schema(description = "Account id")
    private String accountId;

    @Schema(description = "First name")
    private String firstName;

    @Schema(description = "Last name")
    private String lastName;

    @Schema(description = "Usernames")
    private UsernamesDto usernames;

    @Schema(description = "Phone number")
    private String phoneNumber;

    @Schema(description = "Status")
    private UserStatusTdlib status;

    @Schema(description = "Profile photo")
    private ProfilePhotoDto profilePhoto;

    @Schema(description = "Accent color id")
    private Integer accentColorId;

    @Schema(description = "Background custom emoji id")
    private Long backgroundCustomEmojiId;

    @Schema(description = "Profile accent color id")
    private Integer profileAccentColorId;

    @Schema(description = "Profile background custom emoji id")
    private Long profileBackgroundCustomEmojiId;

    @Schema(description = "Emoji status")
    private EmojiStatusDto emojiStatus;

    @Schema(description = "Is contact")
    private Boolean isContact;

    @Schema(description = "Is mutual contact")
    private Boolean isMutualContact;

    @Schema(description = "Is close friend")
    private Boolean isCloseFriend;

    @Schema(description = "Verification status")
    private VerificationStatusDto verificationStatus;

    @Schema(description = "Is premium")
    private Boolean isPremium;

    @Schema(description = "Is support")
    private Boolean isSupport;

    @Schema(description = "Restriction reason")
    private String restrictionReason;

    @Schema(description = "Has active stories")
    private Boolean hasActiveStories;

    @Schema(description = "Has unread active stories")
    private Boolean hasUnreadActiveStories;

    @Schema(description = "Restricts new chats")
    private Boolean restrictsNewChats;

    @Schema(description = "Have access")
    private Boolean haveAccess;

    @Schema(description = "Type")
    private UserTypeTdlib type;

    @Schema(description = "Language code")
    private String languageCode;

    @Schema(description = "Added to attachment menu")
    private Boolean addedToAttachmentMenu;
}
