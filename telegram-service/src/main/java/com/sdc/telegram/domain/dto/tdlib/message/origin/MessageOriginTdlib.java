package com.sdc.telegram.domain.dto.tdlib.message.origin;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.telegram.domain.constants.TelegramOriginType;
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
        @JsonSubTypes.Type(value = MessageOriginChatDto.class, name = TelegramOriginType.CHAT_NAME),
        @JsonSubTypes.Type(value = MessageOriginChanelDto.class, name = TelegramOriginType.CHANEL_NAME),
        @JsonSubTypes.Type(value = MessageOriginHiddenUserDto.class, name = TelegramOriginType.HIDDEN_USER_NAME),
        @JsonSubTypes.Type(value = MessageOriginUserDto.class, name = TelegramOriginType.USER_NAME)
})
@NoArgsConstructor
public abstract class MessageOriginTdlib {
}
