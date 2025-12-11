package com.sdc.telegram.domain.dto;

import lombok.Data;

/**
 * DTO for creating new Telegram credentials
 * @since 12.2025
 */
@Data
public class TelegramCredentialsCreateDto {
    private String apiId;
    private String apiHash;
    private String accountId;
    private String accountName;
    private String phoneNumber;
}
