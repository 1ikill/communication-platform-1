package com.sdc.telegram.domain.dto.tdlib.chat.type;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.telegram.domain.constants.TelegramChatType;
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
        @JsonSubTypes.Type(value = ChatTypePrivateDto.class, name = TelegramChatType.PRIVATE_NAME),
        @JsonSubTypes.Type(value = ChatTypeBasicGroupDto.class, name = TelegramChatType.BASIC_GROUP_NAME),
        @JsonSubTypes.Type(value = ChatTypeSupergroupDto.class, name = TelegramChatType.SUPERGROUP_NAME)
})
@NoArgsConstructor
public abstract class ChatTypeTdlib {
}