package com.sdc.telegram.domain.dto.tdlib.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Authorization state wait password dto")
public class AuthorizationStateWaitPasswordDto extends AuthorizationStateTdlib {
    @Schema(description = "Password hint")
    private String passwordHint;

    @Schema(description = "Has recovery email address")
    private boolean hasRecoveryEmailAddress;

    @Schema(description = "Has passport data")
    private boolean hasPassportData;

    @Schema(description = "Recovery email address pattern")
    private String recoveryEmailAddressPattern;
}
