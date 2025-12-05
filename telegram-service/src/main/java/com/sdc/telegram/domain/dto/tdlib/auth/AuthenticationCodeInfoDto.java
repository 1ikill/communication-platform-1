package com.sdc.telegram.domain.dto.tdlib.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication code info DTO")
public class AuthenticationCodeInfoDto {
    @Schema(description = "Phone number")
    private String phoneNumber;

    @Schema(description = "Type")
    private AuthenticationCodeTypeTdlib type;

    @Schema(description = "Next type")
    private AuthenticationCodeTypeTdlib nextType;

    @Schema(description = "Timeout")
    private int timeout;
}
