package com.sdc.telegram.domain.dto.tdlib.message.interaction;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.telegram.domain.constants.TelegramReplyToType;
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
        @JsonSubTypes.Type(value = MessageReplyToMessageDto.class, name = TelegramReplyToType.MESSAGE_NAME),
})
@NoArgsConstructor
public abstract class MessageReplyToTdlib {
}
