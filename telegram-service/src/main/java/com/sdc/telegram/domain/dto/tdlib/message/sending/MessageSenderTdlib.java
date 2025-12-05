package com.sdc.telegram.domain.dto.tdlib.message.sending;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.telegram.domain.constants.TelegramMessageSenderType;
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
        @JsonSubTypes.Type(value = MessageSenderUserDto.class, name = TelegramMessageSenderType.USER_NAME),
        @JsonSubTypes.Type(value = MessageSenderChatDto.class, name = TelegramMessageSenderType.CHAT_NAME)
})
@NoArgsConstructor
public abstract class MessageSenderTdlib {
}
