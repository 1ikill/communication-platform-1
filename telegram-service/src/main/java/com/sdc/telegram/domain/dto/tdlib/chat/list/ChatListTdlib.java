package com.sdc.telegram.domain.dto.tdlib.chat.list;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.telegram.domain.constants.TelegramChatListType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@NoArgsConstructor
public abstract class ChatListTdlib {
}
