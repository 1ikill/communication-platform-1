package com.sdc.main.domain.dto.telegram.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Authentication code type telegram message DTO")
public class AuthenticationCodeTypeTelegramMessageDto extends AuthenticationCodeTypeTdlib {
    @Schema(description = "Length")
    private int length;
}
