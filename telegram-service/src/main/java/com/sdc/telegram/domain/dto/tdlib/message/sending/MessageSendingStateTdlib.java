package com.sdc.telegram.domain.dto.tdlib.message.sending;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.telegram.domain.constants.TelegramSendingStateType;
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
        @JsonSubTypes.Type(value = MessageSendingStatePendingDto.class, name = TelegramSendingStateType.PENDING_NAME),
        @JsonSubTypes.Type(value = MessageSendingStateFailedDto.class, name = TelegramSendingStateType.FAILED_NAME)
})
@NoArgsConstructor
public abstract class MessageSendingStateTdlib {
}
