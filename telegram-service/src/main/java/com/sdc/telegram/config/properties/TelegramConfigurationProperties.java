package com.sdc.telegram.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Valid
@ConfigurationProperties(prefix = "telegram")
public class TelegramConfigurationProperties {
    @NotNull
    private Boolean useMessageDatabase;

    @NotNull
    private Boolean useSecretChats;

    @NotBlank
    private String systemLanguageCode;

    @NotBlank
    private String deviceModel;

    @NotBlank
    private String applicationVersion;

    @NotBlank
    private String databaseDirectory;
}
