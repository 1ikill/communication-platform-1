package com.sdc.main.domain.dto.telegram.chat.type;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.main.domain.constants.telegram.TelegramChatType;
import lombok.Getter;
import lombok.Setter;

/**
 * Chat type telegram abstract class.
 * @since 12.2025
 */
@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChatTypePrivateDto.class, name = TelegramChatType.PRIVATE_NAME),
        @JsonSubTypes.Type(value = ChatTypeBasicGroupDto.class, name = TelegramChatType.BASIC_GROUP_NAME),
        @JsonSubTypes.Type(value = ChatTypeSupergroupDto.class, name = TelegramChatType.SUPERGROUP_NAME)
})
public abstract class ChatTypeTdlib {
}