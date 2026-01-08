package com.sdc.main.controller;

import com.sdc.main.domain.dto.discord.message.ChannelMessageDto;
import com.sdc.main.domain.dto.discord.message.DiscordPrivateMessageDto;
import com.sdc.main.domain.dto.gmail.GmailMessagesResponseDto;
import com.sdc.main.domain.dto.request.BroadcastMessageRequestDto;
import com.sdc.main.domain.dto.telegram.message.MessageTdlibDto;
import com.sdc.main.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Message-management controller.
 * @since 11.2025
 */
@Slf4j
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @Operation(summary = "Broadcast messages")
    @PostMapping("/broadcast")
    public void sendGroupMessages(
            @RequestBody
            final BroadcastMessageRequestDto request) {
        log.info("Received request POST /messages/broadcast with request:{}", request);
        messageService.broadcastMessages(request);
        log.info("Produced response 200 for POST /messages/broadcast");
    }

    @Operation(summary = "Send telegram message")
    @PostMapping("/telegram/text")
    public void sendTelegramMessage(
            @RequestParam
            final String originalMessage,
            @RequestParam
            final Long chatId,
            @RequestParam
            final String accountId,
            @RequestParam
            final boolean personalize) {
        log.info("Received request POST /messages/telegram/text with originalMessage:{}, chatId:{}, accountId:{}, personalize:{}", originalMessage, chatId, accountId, personalize);
        messageService.sendTelegramMessage(originalMessage, chatId, accountId, personalize);
        log.info("Produced response 200 for POST /messages/telegram/text");
    }

    @Operation(summary = "Send discord private message")
    @PostMapping("/discord/private")
    public void sendDiscordPrivateMessage(
            @RequestParam
            final String originalMessage,
            @RequestParam
            final String chatId,
            @RequestParam
            final Long accountId,
            @RequestParam
            final boolean personalize) {
        log.info("Received request POST /messages/discord/private with originalMessage:{}, chatId:{}, accountId:{}, personalize:{}", originalMessage, chatId, accountId, personalize);
        messageService.sendDiscordPrivateMessage(originalMessage, chatId, accountId, personalize);
        log.info("Produced response 200 for POST /messages/discord/private");
    }

    @Operation(summary = "Send discord channel message")
    @PostMapping("/discord/channel")
    public void sendDiscordChannelMessage(
            @RequestParam
            final String originalMessage,
            @RequestParam
            final String chatId,
            @RequestParam
            final Long accountId,
            @RequestParam
            final boolean personalize) {
        log.info("Received request POST /messages/discord/channel with originalMessage:{}, chatId:{}, accountId:{}, personalize:{}", originalMessage, chatId, accountId, personalize);
        messageService.sendDiscordChannelMessage(originalMessage, chatId, accountId, personalize);
        log.info("Produced response 200 for POST /messages/discord/channel");
    }

    @Operation(summary = "Send gmail message")
    @PostMapping("/gmail/text")
    public void sendGmailMessage(
            @RequestParam
            final String originalMessage,
            @RequestParam
            final String chatId,
            @RequestParam
            final Long accountId,
            @RequestParam
            final String subject,
            @RequestParam
            final boolean personalize) {
        log.info("Received request POST /messages/gmail/text with originalMessage:{}, chatId:{}, accountId:{}, subject:{}, personalize:{}", originalMessage, chatId, accountId, subject, personalize);
        messageService.sendGmailMessage(originalMessage, chatId, accountId, subject, personalize);
        log.info("Produced response 200 for POST /messages/gmail/text");
    }

    @Operation(summary = "Get telegram message")
    @GetMapping("/telegram/{messageId}")
    public MessageTdlibDto getTelegramMessage(
            @RequestParam
            final Long chatId,
            @PathVariable
            final Long messageId,
            @RequestParam
            final String accountId) {
        log.info("Received request GET /messages/telegram/{} with chatId:{}, accountId:{}", messageId, chatId, accountId);
        final MessageTdlibDto result = messageService.getTelegramMessage(chatId, messageId, accountId);
        log.info("Produced response 200 for GET /messages/telegram/{} with body:{}", messageId, result);
        return result;
    }

    @Operation(summary = "Find all telegram chat messages")
    @GetMapping("/telegram/{chatId}/messages")
    public List<MessageTdlibDto> findAllTelegramMessages(
            @PathVariable
            final Long chatId,
            @RequestParam
            final int limit,
            @RequestParam
            final String accountId) {
        log.info("Received request GET /messages/telegram/{}/messages with accountId:{}, limit:{}", chatId, accountId, limit);
        final List<MessageTdlibDto> result = messageService.findAllTelegramMessages(chatId, limit, accountId);
        log.info("Produced response 200 for GET /messages/telegram/{}/messages with body:{}", chatId, result);
        return result;
    }

    @Operation(summary = "Send Telegram image message")
    @PostMapping(value = "/telegram/{chatId}/send-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void sendTelegramImage(
            @PathVariable
            final Long chatId,
            @RequestPart
            final MultipartFile image,
            @RequestParam(required = false)
            final String message,
            @RequestParam
            final String accountId) {
        log.info("Received request GET /messages/telegram/{}/send-image", chatId);
        messageService.sendTelegramImageMessage(chatId, image, message, accountId);
        log.info("Produced response 200 for GET /messages/telegram/{}/send-image", chatId);
    }

    @Operation(summary = "Send Telegram video message")
    @PostMapping(value = "/telegram/{chatId}/send-video",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void sendTelegramVideo(
            @PathVariable
            final Long chatId,
            @RequestPart
            final MultipartFile video,
            @RequestParam(required = false)
            final String message,
            @RequestParam
            final String accountId) {
        log.info("Received request GET /messages/telegram/{}/send-video", chatId);
        messageService.sendTelegramVideoMessage(chatId, video, message, accountId);
        log.info("Produced response 200 for GET /messages/telegram/{}/send-video", chatId);
    }

    @Operation(summary = "Send Telegram document message")
    @PostMapping(value = "/telegram/{chatId}/send-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void sendTelegramDocument(
            @PathVariable
            final Long chatId,
            @RequestPart
            final MultipartFile document,
            @RequestParam(required = false)
            final String message,
            @RequestParam
            final String accountId) {
        log.info("Received request GET /messages/telegram/{}/send-document", chatId);
        messageService.sendTelegramDocumentMessage(chatId, document, message, accountId);
        log.info("Produced response 200 for GET /messages/telegram/{}/send-document", chatId);
    }

    @Operation(summary = "Send gmail file message")
    @PostMapping(value = "/gmail/send-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void sendGmailFileMessage(
            @RequestParam
            final Long accountId,
            @RequestParam
            final String to,
            @RequestParam(required = false, defaultValue = "")
            final String subject,
            @RequestParam(required = false, defaultValue = "")
            final String body,
            @RequestPart
            final MultipartFile file) {
        log.info("Received request GET /messages/gmail/send/file");
        messageService.sendGmailFileMessage(accountId, to, subject, body, file);
        log.info("Produced response 200 for GET /messages/gmail/send/file");
    }

    @Operation(summary = "Get all gmail messages")
    @GetMapping("/gmail/p")
    public GmailMessagesResponseDto getGmailMessages(
            @RequestParam
            final Long accountId,
            @RequestParam
            final Integer maxResults,
            @RequestParam
            final String pageToken) {
        log.info("Received request GET /messages/gmail/p with accountId:{}, maxResults:{}, pageToken:{}", accountId, maxResults, pageToken);
        final GmailMessagesResponseDto result = messageService.getGmailMessages(accountId, maxResults, pageToken);
        log.info("Produced response 200 for GET /messages/gmail/p with body:{}", result);
        return result;
    }

    @Operation(summary = "Get all unread gmail messages")
    @GetMapping("/gmail/unread")
    public GmailMessagesResponseDto getUnreadGmailMessages(
            @RequestParam
            final Long accountId,
            @RequestParam
            final Integer maxResults,
            @RequestParam
            final String pageToken) {
        log.info("Received request GET /messages/gmail/unread with accountId:{}, maxResults:{}, pageToken:{}", accountId, maxResults, pageToken);
        final GmailMessagesResponseDto result = messageService.getUnreadGmailMessages(accountId, maxResults, pageToken);
        log.info("Produced response 200 for GET /messages/gmail/unread with body:{}", result);
        return result;
    }

    @Operation(summary = "Mark gmail message as read")
    @PostMapping("/gmail/{messageId}/read")
    public void markGmailMessageAsRead(
            @RequestParam
            final Long accountId,
            @PathVariable
            final String messageId) {
        log.info("Received request GET /messages/gmail/{}/read with accountId:{}", messageId, accountId);
        messageService.markGmailMessageAsRead(accountId, messageId);
        log.info("Produced response 200 for GET /messages/gmail/{}/read", messageId);
    }

    @Operation(summary = "Mark gmail message as unread")
    @PostMapping("/gmail/{messageId}/unread")
    public void markGmailMessageAsUnread(
            @RequestParam
            final Long accountId,
            @PathVariable
            final String messageId) {
        log.info("Received request GET /messages/gmail/{}/unread with accountId:{}", messageId, accountId);
        messageService.markGmailMessageAsUnread(accountId, messageId);
        log.info("Produced response 200 for GET /messages/gmail/{}/unread", messageId);
    }

    @Operation(summary = "Search gmail messages")
    @GetMapping("/gmail/search")
    public GmailMessagesResponseDto searchGmailMessages(
            @RequestParam
            final Long accountId,
            @RequestParam
            final String query,
            @RequestParam
            final Integer maxResults,
            @RequestParam
            final String pageToken) {
        log.info("Received request GET /messages/gmail/search with accountId:{}, query:{}, maxResults:{}, pageToken:{}", accountId, query, maxResults, pageToken);
        final GmailMessagesResponseDto result = messageService.searchGmailMessages(accountId, query, maxResults, pageToken);
        log.info("Produced response 200 for GET /messages/gmail/search with body:{}", result);
        return result;
    }

    @Operation(summary = "Send Discord private file message")
    @PostMapping(value = "/discord/dm/{userId}/send-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void sendDiscordPrivateFileMessage(
            @RequestParam
            final Long botId,
            @PathVariable
            final String userId,
            @RequestPart
            final List<MultipartFile> files,
            @RequestParam(required = false, defaultValue = "") final String message) {
        log.info("Received request GET /messages/discord/dm/{}/send-file with botId:{}", userId, botId);
        messageService.sendDiscordPrivateFileMessage(botId, userId, files, message);
        log.info("Produced response 200 for GET /messages/discord/dm/{}/send-file", userId);
    }

    @Operation(summary = "Send Discord guild channel file message")
    @PostMapping(value = "/discord/channel/{channelId}/send-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void sendDiscordChannelFileMessage(
            @RequestParam
            final Long botId,
            @PathVariable
            final String channelId,
            @RequestPart
            final List<MultipartFile> files,
            @RequestParam(required = false, defaultValue = "")
            final String message) {
        log.info("Received request GET /messages/discord/channel/{}/send-file with botId:{}", channelId, botId);
        messageService.sendDiscordGuildFileMessage(botId, channelId, files, message);
        log.info("Produced response 200 for GET /messages/discord/channel/{}/send-file", channelId);
    }

    @Operation(summary = "Get Discord guild channel message history")
    @GetMapping("/discord/channel/{channelId}/history")
    public List<ChannelMessageDto> getDiscordGuildChannelMessageHistory(
            @RequestParam
            final Long botId,
            @PathVariable
            final String channelId,
            @RequestParam
            final int limit) {
        log.info("Received request GET /messages/discord/channel/{}/history with botId:{}, limit:{}", channelId, botId, limit);
        final List<ChannelMessageDto> result = messageService.getDiscordGuildChannelMessageHistory(botId, channelId, limit);
        log.info("Produced response 200 for GET /messages/discord/channel/{}/history with body:{}", channelId, result);
        return result;
    }

    @Operation(summary = "Get Discord private message history")
    @GetMapping("/discord/dm/{channelId}/history")
    public List<DiscordPrivateMessageDto> getDiscordPrivateMessageHistory(
            @RequestParam
            final Long botId,
            @PathVariable
            final String channelId,
            @RequestParam
            final int limit) {
        log.info("Received request GET /messages/discord/dm/{}/history with botId:{}, limit:{}", channelId, botId, limit);
        final List<DiscordPrivateMessageDto> result = messageService.getDiscordPrivateMessageHistory(botId, channelId, limit);
        log.info("Produced response 200 for GET m/essages/discord/dm/{}/history with body:{}", channelId, result);
        return result;
    }

    @Operation(summary = "Search Discord private messages")
    @GetMapping("/discord/dm/search")
    public List<DiscordPrivateMessageDto> searchDiscordPrivateMessages(
            @RequestParam
            final Long botId,
            @RequestParam
            final String channelId,
            @RequestParam
            final String query) {
        log.info("Received request GET /messages/discord/dm/search with botId:{}, channelId:{}, query:{}", botId, channelId, query);
        final List<DiscordPrivateMessageDto> result = messageService.searchDiscordPrivateMessages(botId, channelId, query);
        log.info("Produced response 200 for GET /messages/discord/dm/search with body:{}", result);
        return result;
    }

    @Operation(summary = "Search Discord guild channel messages")
    @GetMapping("/discord/channel/search")
    public List<ChannelMessageDto> searchDiscordGuildChannelMessages(
            @RequestParam
            final Long botId,
            @RequestParam
            final String channelId,
            @RequestParam
            final String query) {
        log.info("Received request GET /messages/discord/channel/search with botId:{}, channelId:{}, query:{}", botId, channelId, query);
        final List<ChannelMessageDto> result = messageService.searchDiscordGuildChannelMessages(botId, channelId, query);
        log.info("Produced response 200 for GET /messages/discord/channel/search with body:{}", result);
        return result;
    }

    @Operation(summary = "Delete Discord private message")
    @DeleteMapping("/discord/dm/delete")
    public void deleteDiscordPrivateMessage(
            @RequestParam
            final Long botId,
            @RequestParam
            final List<Long> messageIds) {
        log.info("Received request DELETE /messages/discord/dm/delete with botId:{}, messageIds:{}", botId, messageIds);
        messageService.deleteDiscordPrivateMessage(botId, messageIds);
        log.info("Produced response 200 for DELETE /messages/discord/dm/delete");
    }

    @Operation(summary = "Delete Discord channel message")
    @DeleteMapping("/discord/channel/{channelId}/delete")
    public void deleteDiscordPrivateMessage(
            @RequestParam
            final Long botId,
            @PathVariable
            final String channelId,
            @RequestParam
            final List<String> messageIds) {
        log.info("Received request DELETE /messages/discord/channel/{}/delete with botId:{}, messageIds:{}", channelId, botId, messageIds);
        messageService.deleteDiscordGuildChannelMessages(botId, messageIds, channelId);
        log.info("Produced response 200 for DELETE /messages/discord/channel/{}/delete", channelId);
    }

    @Operation(summary = "Update Discord private message")
    @PutMapping("/discord/dm/{messageId}/update")
    public void updateDiscordPrivateMessage(
            @RequestParam
            final Long botId,
            @PathVariable
            final Long messageId,
            @RequestParam
            final String updatedMessage) {
        log.info("Received request PUT /messages/discord/dm/{}/update with botId:{}, updatedMessage:{}", messageId, botId, updatedMessage);
        messageService.updateDiscordPrivateMessage(botId, messageId, updatedMessage);
        log.info("Produced response 200 for PUT /messages/discord/dm/{}/update", messageId);
    }

    @Operation(summary = "Update Discord private message")
    @PutMapping("/discord/channel/{channelId}/update")
    public void updateDiscordPrivateMessage(
            @RequestParam
            final Long botId,
            @PathVariable
            final String channelId,
            @RequestParam
            final String messageId,
            @RequestParam
            final String updatedMessage) {
        log.info("Received request PUT /messages/discord/channel/{}/update with botId:{}, messageId:{} updatedMessage:{}", channelId, botId, messageId, updatedMessage);
        messageService.updateDiscordGuildChannelMessage(botId, channelId, messageId, updatedMessage);
        log.info("Produced response 200 for PUT /messages/discord/channel/{}/update", channelId);
    }

}
