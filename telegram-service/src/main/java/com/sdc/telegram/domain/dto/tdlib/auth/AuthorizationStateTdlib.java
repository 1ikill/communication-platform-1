package com.sdc.telegram.domain.dto.tdlib.auth;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.telegram.domain.constants.TelegramAuthorizationStateType;
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
        @JsonSubTypes.Type(value = AuthorizationStateReadyDto.class, name = TelegramAuthorizationStateType.READY_NAME),
        @JsonSubTypes.Type(value = AuthorizationStateWaitPhoneNumberDto.class, name = TelegramAuthorizationStateType.WAIT_PHONE_NUMBER_NAME),
        @JsonSubTypes.Type(value = AuthorizationStateWaitCodeDto.class, name = TelegramAuthorizationStateType.WAIT_CODE_NAME),
        @JsonSubTypes.Type(value = AuthorizationStateWaitPasswordDto.class, name = TelegramAuthorizationStateType.WAIT_PASSWORD_NAME)
})
@NoArgsConstructor
public abstract class AuthorizationStateTdlib {
}
