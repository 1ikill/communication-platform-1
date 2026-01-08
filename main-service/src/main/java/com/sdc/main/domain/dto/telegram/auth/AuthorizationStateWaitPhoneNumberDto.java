package com.sdc.main.domain.dto.telegram.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Authorization state wait phone number dto")
public class AuthorizationStateWaitPhoneNumberDto extends AuthorizationStateTdlib {
}
