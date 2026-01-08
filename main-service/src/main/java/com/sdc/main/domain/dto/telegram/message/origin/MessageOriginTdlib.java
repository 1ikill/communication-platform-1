package com.sdc.main.domain.dto.telegram.message.origin;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.main.domain.constants.telegram.TelegramOriginType;
import lombok.Getter;
import lombok.Setter;


/**
 * Message origin telegram abstract class.
 * @since 12.2025
 */
@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MessageOriginChatDto.class, name = TelegramOriginType.CHAT_NAME),
        @JsonSubTypes.Type(value = MessageOriginChanelDto.class, name = TelegramOriginType.CHANEL_NAME),
        @JsonSubTypes.Type(value = MessageOriginHiddenUserDto.class, name = TelegramOriginType.HIDDEN_USER_NAME),
        @JsonSubTypes.Type(value = MessageOriginUserDto.class, name = TelegramOriginType.USER_NAME)
})
public abstract class MessageOriginTdlib {
}
