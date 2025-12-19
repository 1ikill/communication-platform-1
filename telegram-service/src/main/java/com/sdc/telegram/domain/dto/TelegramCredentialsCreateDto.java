package com.sdc.telegram.domain.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for creating new Telegram credentials
 * @since 12.2025
 */
@Data
@Schema(description = "Telegram credentials create DTO")
public class TelegramCredentialsCreateDto {
    @NotBlank(message = "Api Id must not be blank")
    private String apiId;

    @NotBlank(message = "Api Hash must not be blank")
    private String apiHash;

    @NotBlank(message = "Account Id must not be blank")
    private String accountId;

    @NotBlank(message = "Account Name must not be blank")
    private String accountName;

    @NotBlank(message = "Phone Number must not be blank")
    private String phoneNumber;
}
