package com.sdc.main.domain.dto.telegram.chat.list;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.main.domain.constants.telegram.TelegramChatListType;
import lombok.Getter;
import lombok.Setter;

/**
 * Chat list telegram abstract class.
 * @since 12.2025
 */
@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChatListMainDto.class, name = TelegramChatListType.MAIN_NAME),
        @JsonSubTypes.Type(value = ChatListFolderDto.class, name = TelegramChatListType.FOLDER_NAME),
        @JsonSubTypes.Type(value = ChatListArchiveDto.class, name = TelegramChatListType.ARCHIVE_NAME)
})
public abstract class ChatListTdlib {
}
