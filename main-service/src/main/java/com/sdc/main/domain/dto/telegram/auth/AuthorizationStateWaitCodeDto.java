package com.sdc.main.domain.dto.telegram.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Authorization state wait code DTO")
public class AuthorizationStateWaitCodeDto extends AuthorizationStateTdlib {
    @Schema(description = "Code info")
    private AuthenticationCodeInfoDto codeInfo;
}
