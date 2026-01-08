package com.sdc.main.domain.dto.telegram.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Authorization state ready DTO")
public class AuthorizationStateReadyDto extends AuthorizationStateTdlib {
}
