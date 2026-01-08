package com.sdc.main.domain.dto.telegram.auth;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.main.domain.constants.telegram.TelegramAuthorizationStateType;
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
