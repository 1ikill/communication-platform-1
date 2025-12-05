package com.sdc.telegram.domain.dto;

import lombok.Data;

@Data
public class TelegramCredentialsCreateDto {
    private String apiId;
    private String apiHash;
    private String accountId;
    private String accountName;
    private String phoneNumber;
}
