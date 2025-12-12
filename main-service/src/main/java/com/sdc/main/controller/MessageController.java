package com.sdc.main.controller;

import com.sdc.main.domain.dto.request.BroadcastMessageRequestDto;
import com.sdc.main.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        messageService.sendDiscordPrivateMessage(originalMessage, chatId, accountId, personalize);
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
}
