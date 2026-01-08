package com.sdc.main.domain.dto.telegram.message.interaction;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.main.domain.constants.telegram.TelegramReplyToType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Message reply to telegram abstract class.
 * @since 12.2025
 */
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
