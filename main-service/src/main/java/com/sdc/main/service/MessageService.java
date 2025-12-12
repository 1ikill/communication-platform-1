package com.sdc.main.service;

import com.sdc.main.domain.constants.DiscordMessageType;
import com.sdc.main.domain.dto.request.BroadcastMessageRequestDto;
import com.sdc.main.domain.dto.request.DiscordMessageRequestDto;
import com.sdc.main.domain.dto.request.GmailMessageRequestDto;
import com.sdc.main.domain.dto.request.MessageRequestDto;
import com.sdc.main.domain.dto.request.TelegramMessageRequestDto;
import com.sdc.main.domain.mapper.MessageRequestMapper;
import com.sdc.main.integration.client.AIServiceClient;
import com.sdc.main.integration.client.DiscordServiceClient;
import com.sdc.main.integration.client.GmailServiceClient;
import com.sdc.main.integration.client.TelegramServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            message = aiClient.customizeMessage(originalMessage, TELEGRAM, chatId);
        }
        gmailClient.sendTextMessage(chatId, message, accountId, subject);
    }

    public void sendDiscordChannelMessage(final String originalMessage, final String chatId, final Long accountId,
                                 final boolean personalize) {
        String message = originalMessage;
        if (personalize) {
            message = aiClient.customizeMessage(originalMessage, TELEGRAM, chatId);
        }
        discordClient.sendChannelMessage(chatId, message, accountId);
    }

    public void sendDiscordPrivateMessage(final String originalMessage, final String chatId, final Long accountId,
                                          final boolean personalize) {
        String message = originalMessage;
        if (personalize) {
            message = aiClient.customizeMessage(originalMessage, TELEGRAM, chatId);
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

    private Map<String, String> getPersonalizedMessages(final List<MessageRequestDto> request) {
        final Map<String, String> processedMessages = new HashMap<>();
        for (MessageRequestDto messageRequestDto : request) {
            final String processedMessage = aiClient.customizeMessage(messageRequestDto.getMessage(), messageRequestDto.getPlatform(), messageRequestDto.getChatIdentifier());
            processedMessages.put(messageRequestDto.getChatIdentifier(), processedMessage);
        }
        return processedMessages;
    }
}
