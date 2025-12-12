package com.sdc.main.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Microservices integration configuration properties.
 * @since 11.2025
 */
@Data
@Validated
@ConfigurationProperties("services")
public class MicroserviceIntegrationProperties {

    @NotNull
    private UserService userService;

    @NotNull
    private TelegramService telegramService;

    @NotNull
    private AIService aiService;

    @NotNull
    private GmailService gmailService;

    @NotNull
    private DiscordService discordService;

    @Data
    @Validated
    public static class UserService {
        @URL
        @NotBlank
        private String baseUrl;

        @URL
        @NotBlank
        private String registerUrl;

        @URL
        @NotBlank
        private String loginUrl;

        @URL
        @NotBlank
        private String refreshUrl;

        @URL
        @NotBlank
        private String createUserAdminUrl;

        @URL
        @NotBlank
        private String patchUserUrl;

        @URL
        @NotBlank
        private String getMeUrl;

    }

    @Data
    @Validated
    public static class TelegramService {
        @URL
        @NotBlank
        private String baseUrl;

        @URL
        @NotBlank
        private String getChatFoldersUrl;

        @URL
        @NotBlank
        private String getChatsFolderUrl;

        @URL
        @NotBlank
        private String getChatMessagesUrl;

        @URL
        @NotBlank
        private String sendTextMessageUrl;

        @URL
        @NotBlank
        private String sendImageMessageUrl;

        @URL
        @NotBlank
        private String sendVideoMessageUrl;
    }

    @Data
    @Validated
    public static class AIService {
        @URL
        @NotBlank
        private String baseUrl;

        @URL
        @NotBlank
        private String addContactProfileUrl;

        @URL
        @NotBlank
        private String patchContactProfileUrl;

        @URL
        @NotBlank
        private String formatMessageUrl;
    }

    @Data
    @Validated
    public static class GmailService {
        @URL
        @NotBlank
        private String baseUrl;

        @URL
        @NotBlank
        private String sendTextMessageUrl;
    }

    @Data
    @Validated
    public static class DiscordService {
        @URL
        @NotBlank
        private String baseUrl;

        @NotBlank
        private String sendChannelMessageUrl;

        @NotBlank
        private String sendDirectMessageUrl;
    }
}
