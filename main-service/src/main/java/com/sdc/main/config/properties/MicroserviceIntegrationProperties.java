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
        private String getChatsMainUrl;

        @URL
        @NotBlank
        private String getChatsArchiveUrl;

        @URL
        @NotBlank
        private String getAccountInfoUrl;

        @URL
        @NotBlank
        private String getAccountsInfoUrl;

        @URL
        @NotBlank
        private String changeProfileImageUrl;

        @URL
        @NotBlank
        private String getChatIdUrl;

        @URL
        @NotBlank
        private String createChatUrl;

        @URL
        @NotBlank
        private String getImageUrl;

        @URL
        @NotBlank
        private String getVideoUrl;

        @URL
        @NotBlank
        private String getDocumentUrl;

        @URL
        @NotBlank
        private String getNotificationsUrl;

        @URL
        @NotBlank
        private String createEmptyChatUrl;

        @URL
        @NotBlank
        private String deleteEmptyChatUrl;

        @URL
        @NotBlank
        private String getChatFoldersUrl;

        @URL
        @NotBlank
        private String getChatsFolderUrl;

        @URL
        @NotBlank
        private String sendTextMessageUrl;

        @URL
        @NotBlank
        private String sendImageMessageUrl;

        @URL
        @NotBlank
        private String sendVideoMessageUrl;

        @URL
        @NotBlank
        private String sendDocumentMessageUrl;

        @URL
        @NotBlank
        private String sendPhoneUrl;

        @URL
        @NotBlank
        private String sendCodeUrl;

        @URL
        @NotBlank
        private String sendPasswordUrl;

        @URL
        @NotBlank
        private String getAuthStateUrl;

        @URL
        @NotBlank
        private String logoutUrl;

        @URL
        @NotBlank
        private String addCredentialsUrl;
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

        @URL
        @NotBlank
        private String getAuthUrl;

        @URL
        @NotBlank
        private String sendFileUrl;

        @URL
        @NotBlank
        private String getMeUrl;

        @URL
        @NotBlank
        private String readerBaseUrl;
    }

    @Data
    @Validated
    public static class DiscordService {
        @URL
        @NotBlank
        private String baseUrl;

        @URL
        @NotBlank
        private String getBotsUrl;

        @URL
        @NotBlank
        private String addBotUrl;

        @NotBlank
        private String getPrivateChatsUrl;

        @NotBlank
        private String getGuildsUrl;

        @NotBlank
        private String getUsersUrl;

        @NotBlank
        private String searchChatsUrl;

        @URL
        @NotBlank
        private String getImageUrl;

        @URL
        @NotBlank
        private String getVideoUrl;

        @URL
        @NotBlank
        private String getDocumentUrl;

        @NotBlank
        private String sendChannelFileMessageUrl;

        @NotBlank
        private String sendPrivateFileMessageUrl;

        @NotBlank
        private String deletePrivateMessageUrl;

        @NotBlank
        private String deleteGuildMessageUrl;

        @NotBlank
        private String sendChannelMessageUrl;

        @NotBlank
        private String sendDirectMessageUrl;
    }
}
