package com.sdc.main.service;

import com.sdc.main.domain.dto.discord.message.ChannelMessageDto;
import com.sdc.main.domain.dto.discord.message.DiscordPrivateMessageDto;
import com.sdc.main.domain.dto.gmail.GmailMessagesResponseDto;
import com.sdc.main.domain.dto.request.BroadcastMessageRequestDto;
import com.sdc.main.domain.dto.request.DiscordMessageRequestDto;
import com.sdc.main.domain.dto.request.GmailMessageRequestDto;
import com.sdc.main.domain.dto.request.MessageRequestDto;
import com.sdc.main.domain.dto.request.TelegramMessageRequestDto;
import com.sdc.main.domain.dto.telegram.message.MessageTdlibDto;
import com.sdc.main.domain.mapper.MessageRequestMapper;
import com.sdc.main.integration.client.AIServiceClient;
import com.sdc.main.integration.client.DiscordServiceClient;
import com.sdc.main.integration.client.GmailServiceClient;
import com.sdc.main.integration.client.TelegramServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sdc.main.domain.constants.CommunicationPlatformType.DISCORD;
import static com.sdc.main.domain.constants.CommunicationPlatformType.EMAIL;
import static com.sdc.main.domain.constants.CommunicationPlatformType.TELEGRAM;

/**
 * Message-management service.
 * @since 11.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    private final TelegramServiceClient telegramClient;
    private final AIServiceClient aiClient;
    private final GmailServiceClient gmailClient;
    private final DiscordServiceClient discordClient;
    private final MessageRequestMapper messageRequestMapper;

    public MessageTdlibDto getTelegramMessage(final Long chatId, final Long messageId, final String accountId) {
        return telegramClient.getTelegramMessage(chatId, messageId, accountId);
    }

    public List<MessageTdlibDto> findAllTelegramMessages(final Long chatId, final int limit, final String accountId) {
        return telegramClient.findAllMessages(chatId, limit, accountId);
    }

    public void sendTelegramMessage(final String originalMessage, final Long chatId, final String accountId,
                                                final boolean personalize) {
        String message = originalMessage;
        if (personalize) {
            message = aiClient.customizeMessage(originalMessage, TELEGRAM, chatId.toString());
        }
        telegramClient.sendTextMessage(chatId, message, accountId);
    }

    public void sendGmailMessage(final String originalMessage, final String chatId, final Long accountId, final String subject,
                                 final boolean personalize) {
        String message = originalMessage;
        if (personalize) {
            message = aiClient.customizeMessage(originalMessage, EMAIL, chatId);
        }
        gmailClient.sendTextMessage(chatId, message, accountId, subject);
    }

    public void sendDiscordChannelMessage(final String originalMessage, final String chatId, final Long accountId,
                                 final boolean personalize) {
        String message = originalMessage;
        if (personalize) {
            message = aiClient.customizeMessage(originalMessage, DISCORD, chatId);
        }
        discordClient.sendChannelMessage(chatId, message, accountId);
    }

    public void sendDiscordPrivateMessage(final String originalMessage, final String chatId, final Long accountId,
                                          final boolean personalize) {
        String message = originalMessage;
        if (personalize) {
            message = aiClient.customizeMessage(originalMessage, DISCORD, chatId);
        }
        discordClient.sendDirectMessage(chatId, message, accountId);
    }

    public void broadcastMessages(final BroadcastMessageRequestDto requestDto) {
        if (requestDto.getPersonalize()) {
            final Map<String, String> processedMessages = getPersonalizedMessages(requestDto.getReceivers());
            sendPersonalizedMessages(requestDto.getReceivers(), processedMessages);
            return;
        }
        requestDto.getReceivers().forEach(this::sendMessage);
    }

    private void sendPersonalizedMessages(final List<MessageRequestDto> requestDtos, final Map<String, String> processedMessages) {
        for (MessageRequestDto request : requestDtos) {
            final MessageRequestDto personalizedRequest = messageRequestMapper.fromDto(request, processedMessages.get(request.getChatIdentifier()));
            sendMessage(personalizedRequest);
        }
    }

    private void sendMessage(final MessageRequestDto request) {
        if (request instanceof TelegramMessageRequestDto telegramRequest) {
            telegramClient.sendTextMessage(Long.valueOf(telegramRequest.getChatIdentifier()), telegramRequest.getMessage(), telegramRequest.getAccountId());
        } else if (request instanceof GmailMessageRequestDto gmailRequest) {
            gmailClient.sendTextMessage(gmailRequest.getChatIdentifier(), gmailRequest.getMessage(), gmailRequest.getAccountId(), gmailRequest.getSubject());
        } else if (request instanceof DiscordMessageRequestDto discordRequest) {
            switch (discordRequest.getMessageType()) {
                case CHANNEL :
                    discordClient.sendChannelMessage(discordRequest.getChatIdentifier(), discordRequest.getMessage(), discordRequest.getAccountId());
                    break;

                case PRIVATE:
                    discordClient.sendDirectMessage(discordRequest.getChatIdentifier(), discordRequest.getMessage(), discordRequest.getAccountId());
                    break;

                default:
                    throw new RuntimeException("Unknown discord message type");
            }
        }
    }

    public void sendTelegramImageMessage(final Long chatId, final MultipartFile image, final String message, final String accountId) {
        telegramClient.sendImageMessage(chatId, image, message, accountId);
    }

    public void sendTelegramVideoMessage(final Long chatId, final MultipartFile video, final String message, final String accountId) {
        telegramClient.sendVideoMessage(chatId, video, message, accountId);
    }

    public void sendTelegramDocumentMessage(final Long chatId, final MultipartFile document, final String message, final String accountId) {
        telegramClient.sendDocumentMessage(chatId, document, message, accountId);
    }

    public void sendGmailFileMessage(final Long accountId, final String to, final String subject, final String body, final MultipartFile file) {
        gmailClient.sendFile(accountId, to, subject, body, file);
    }

    public GmailMessagesResponseDto getGmailMessages(final Long accountId, final Integer maxResults, final String pageToken) {
        return gmailClient.getMessages(accountId, maxResults, pageToken);
    }

    public GmailMessagesResponseDto getUnreadGmailMessages(final Long accountId, final Integer maxResults, final String pageToken) {
        return gmailClient.getUnreadMessage(accountId, maxResults, pageToken);
    }

    public void markGmailMessageAsRead(final Long accountId, final String messageId) {
        gmailClient.markAsRead(accountId, messageId);
    }

    public void markGmailMessageAsUnread(final Long accountId, final String messageId) {
        gmailClient.markAsUnread(accountId, messageId);
    }

    public GmailMessagesResponseDto searchGmailMessages(final Long accountId, final String query, final Integer maxResults, final String pageToken) {
        return gmailClient.searchMessages(accountId, query, maxResults, pageToken);
    }

    public void sendDiscordPrivateFileMessage(final Long botId, final String userId, final List<MultipartFile> files, final String message) {
        discordClient.sendDmFileMessage(botId, userId, files, message);
    }

    public void sendDiscordGuildFileMessage(final Long botId, final String channelId, final List<MultipartFile> files, final String message) {
        discordClient.sendChannelFileMessage(botId, channelId, files, message);
    }

    public List<ChannelMessageDto> getDiscordGuildChannelMessageHistory(final Long botId, final String channelId, final int limit) {
        return discordClient.getGuildChannelHistory(botId, channelId, limit);
    }

    public List<DiscordPrivateMessageDto> getDiscordPrivateMessageHistory(final Long botId, final String channelId, final int limit) {
        return discordClient.getHistory(botId, channelId, limit);
    }

    public List<DiscordPrivateMessageDto> searchDiscordPrivateMessages(final Long botId, final String channelId, final String query) {
        return discordClient.searchPrivateMessages(botId, channelId, query);
    }

    public List<ChannelMessageDto> searchDiscordGuildChannelMessages(final Long botId, final String channelId, final String query) {
        return discordClient.searchGuildMessages(botId, channelId, query);
    }

    public void deleteDiscordPrivateMessage(final Long botId, final List<Long> messageIds) {
        discordClient.deletePrivateMessage(botId, messageIds);
    }

    public void deleteDiscordGuildChannelMessages(final Long botId, final List<String> messageIds, final String channelId) {
        discordClient.deleteGuildMessage(botId, messageIds, channelId);
    }

    public void updateDiscordPrivateMessage(final Long botId, final Long messageId, final String updatedMessage) {
        discordClient.updateMessage(botId, messageId, updatedMessage);
    }

    public void updateDiscordGuildChannelMessage(final Long botId, final String channelId, final String messageId, final String updatedMessage) {
        discordClient.updateGuildMessage(botId, channelId, messageId, updatedMessage);
    }

    private Map<String, String> getPersonalizedMessages(final List<MessageRequestDto> request) {
        final Map<String, String> processedMessages = new HashMap<>();
        for (MessageRequestDto messageRequestDto : request) {
            final String processedMessage = aiClient.customizeMessage(messageRequestDto.getMessage(), messageRequestDto.getPlatform(), messageRequestDto.getChatIdentifier());
            processedMessages.put(messageRequestDto.getChatIdentifier(), processedMessage);
        }
        return processedMessages;
    }
}
