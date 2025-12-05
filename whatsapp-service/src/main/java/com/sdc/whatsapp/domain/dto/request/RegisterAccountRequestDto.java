package com.sdc.whatsapp.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAccountRequestDto {
    private String displayName;
    private String accessToken;
    private String phoneNumberId;
    private String wabaId;
}
