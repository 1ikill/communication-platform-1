package com.sdc.main.service;

import com.sdc.main.domain.dto.discord.request.GetFileRequestDto;
import com.sdc.main.integration.client.AIServiceClient;
import com.sdc.main.integration.client.DiscordServiceClient;
import com.sdc.main.integration.client.GmailServiceClient;
import com.sdc.main.integration.client.TelegramServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * File-management service.
 * @since 12.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    private final TelegramServiceClient telegramClient;
    private final AIServiceClient aiClient;
    private final GmailServiceClient gmailClient;
    private final DiscordServiceClient discordClient;

    public byte[] getTelegramImage(final String remoteId, final String accountId) {
        return telegramClient.getTelegramImage(remoteId, accountId);
    }

    public StreamingResponseBody getTelegramVideo(final String remoteId, final String accountId) {
        return telegramClient.getTelegramVideo(remoteId, accountId);
    }

    public StreamingResponseBody getTelegramDocument(final String remoteId, final String accountId) {
        return telegramClient.getTelegramDocument(remoteId, accountId);
    }

    public StreamingResponseBody getGmailImage(final Long accountId, final String messageId, final String attachmentId) {
        return gmailClient.downloadImage(accountId, messageId, attachmentId);
    }

    public StreamingResponseBody getGmailVideo(final Long accountId, final String messageId, final String attachmentId) {
        return gmailClient.downloadVideo(accountId, messageId, attachmentId);
    }

    public StreamingResponseBody getGmailDocument(final Long accountId, final String messageId, final String attachmentId) {
        return gmailClient.downloadDocument(accountId, messageId, attachmentId);
    }

    public StreamingResponseBody getDiscordImage(final GetFileRequestDto request) {
        return discordClient.getImage(request);
    }

    public StreamingResponseBody getDiscordVideo(final GetFileRequestDto request) {
        return discordClient.getVideo(request);
    }

    public StreamingResponseBody getDiscordDocument(final GetFileRequestDto request) {
        return discordClient.getDocument(request);
    }
}
