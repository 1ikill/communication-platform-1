package com.sdc.main.domain.dto.telegram.message.sending;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.main.domain.constants.telegram.TelegramMessageSenderType;
import lombok.Getter;
import lombok.Setter;

/**
 * Message sender telegram abstract class.
 * @since 12.2025
 */
@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MessageSenderUserDto.class, name = TelegramMessageSenderType.USER_NAME),
        @JsonSubTypes.Type(value = MessageSenderChatDto.class, name = TelegramMessageSenderType.CHAT_NAME)
})
public abstract class MessageSenderTdlib {
}
