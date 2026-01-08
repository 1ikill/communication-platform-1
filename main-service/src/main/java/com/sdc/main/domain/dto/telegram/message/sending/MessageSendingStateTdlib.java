package com.sdc.main.domain.dto.telegram.message.sending;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.main.domain.constants.telegram.TelegramSendingStateType;
import lombok.Getter;
import lombok.Setter;

/**
 * Message sending state telegram abstract class.
 * @since 12.2025
 */
@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MessageSendingStatePendingDto.class, name = TelegramSendingStateType.PENDING_NAME),
        @JsonSubTypes.Type(value = MessageSendingStateFailedDto.class, name = TelegramSendingStateType.FAILED_NAME)
})
public abstract class MessageSendingStateTdlib {
}
