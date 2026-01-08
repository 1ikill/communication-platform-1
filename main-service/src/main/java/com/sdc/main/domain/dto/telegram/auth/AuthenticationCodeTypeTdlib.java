package com.sdc.main.domain.dto.telegram.auth;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.main.domain.constants.telegram.TelegramAuthenticationCodeType;
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
        @JsonSubTypes.Type(value = AuthenticationCodeTypeTelegramMessageDto.class, name = TelegramAuthenticationCodeType.TELEGRAM_MESSAGE_NAME),
        @JsonSubTypes.Type(value = AuthenticationCodeTypeSmsDto.class, name = TelegramAuthenticationCodeType.SMS_NAME)
})
@NoArgsConstructor
public abstract class AuthenticationCodeTypeTdlib {
}
