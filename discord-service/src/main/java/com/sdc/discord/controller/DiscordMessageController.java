package com.sdc.discord.controller;

import com.sdc.discord.domain.dto.message.ChannelMessageDto;
import com.sdc.discord.domain.dto.message.DiscordPrivateMessageDto;
import com.sdc.discord.service.DiscordMessageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/discord/messages/{botId}")
@RequiredArgsConstructor
@Validated
public class DiscordMessageController {
    private final DiscordMessageService dmService;

    @Operation(summary = "Send direct message")
    @PostMapping("/dm/send")
    public void sendDirectMessage(
            @PathVariable
            final Long botId,
            @RequestParam
            final String userId,
            @RequestParam
            final String message) {
        log.info("Received request POST /api/discord/messages/{}/dm/send with userId:{}", botId, userId);
        dmService.sendDirectMessage(botId, userId, message);
        log.info("Produced response 200 for POST /api/discord/messages/{}/dm/send", botId);
    }

    @Operation(summary = "Send guild channel message")
    @PostMapping("/channel/send")
    public void sendChannelMessage(
            @PathVariable
            final Long botId,
            @RequestParam
            final String channelId,
            @RequestParam
            final String message) {
        log.info("Received request POST /api/discord/messages/{}/channel/send with channelId:{}", botId, channelId);
        dmService.sendChannelMessage(botId, channelId, message);
        log.info("Produced response 200 for POST /api/discord/messages/{}/channel/send", botId);
    }

    @Operation(summary = "Send guild channel file message")
    @PostMapping("/channel/send-file")
    public void sendChannelFileMessage(
            @PathVariable
            final Long botId,
            @RequestParam
            final String channelId,
            @RequestParam
            final List<MultipartFile> files,
            @RequestParam(required = false)
            final String message) throws IOException {
        log.info("Received request POST /api/discord/messages/{}/channel/send-file with channelId:{}", botId, channelId);
        dmService.sendChannelFileMessage(botId, channelId, files, message);
        log.info("Produced response 200 for POST /api/discord/messages/{}/channel/send-file", botId);
    }

    @Operation(summary = "Send direct file message")
    @PostMapping("/dm/send-file")
    public void sendDmFileMessage(
            @PathVariable
            final Long botId,
            @RequestParam
            final String userId,
            @RequestParam
            final List<MultipartFile> files,
            @RequestParam(required = false)
            final String message) throws IOException {
        log.info("Received request POST /api/discord/messages/{}/dm/send-file with userId:{}", botId, userId);
        dmService.sendDirectFileMessage(botId, userId, files, message);
        log.info("Produced response 200 for POST /api/discord/messages/{}/dm/send-file", botId);
    }

    @Operation(summary = "Get guild channel message history")
    @GetMapping("/channels/{channelId}/history")
    public List<ChannelMessageDto> getGuildChannelHistory(
            @PathVariable
            final Long botId,
            @PathVariable
            final String channelId,
            @RequestParam(defaultValue = "50")
            final int limit) {
        log.info("Received request GET /api/discord/messages/{}/channels/{}/history with limit:{}", botId, channelId, limit);
        final List<ChannelMessageDto> result = dmService.getGuildChannelHistory(botId, channelId, limit);
        log.info("Produced response 200 for GET /api/discord/messages/{}/channels/{}/history", botId, channelId);
        return result;
    }

    @Operation(summary = "Get private chat message history")
    @GetMapping("/dm/{channelId}/history")
    public List<DiscordPrivateMessageDto> getHistory(
            @PathVariable
            final Long botId,
            @PathVariable
            final String channelId,
            @RequestParam(defaultValue = "50")
            final int limit) {
        log.info("Received request GET /api/discord/messages/{}/dm/{}/history with limit:{}", botId, channelId, limit);
        final List<DiscordPrivateMessageDto> messages = dmService.getPrivateMessageHistory(botId, channelId, limit);
        log.info("Produced response 200 for GET /api/discord/messages/{}/dm/{}/history", botId, channelId);
        return messages;
    }

    @Operation(summary = "Search messages in private chat")
    @GetMapping("/dm/{channelId}/search")
    public List<DiscordPrivateMessageDto> searchMessages(
            @PathVariable
            final Long botId,
            @PathVariable
            final String channelId,
            @RequestParam
            final String query) {
        log.info("Received request GET /api/discord/messages/{}/dm/{}/search with query:{}", botId, channelId, query);
        final List<DiscordPrivateMessageDto> result = dmService.searchMessages(botId, channelId, query);
        log.info("Produced response 200 for GET /api/discord/messages/{}/dm/{}/search", botId, channelId);
        return result;
    }

    @Operation(summary = "Search messages in guild channel")
    @GetMapping("/channel/{channelId}/search")
    public List<ChannelMessageDto> searchGuildChannelMessages(
            @PathVariable
            final Long botId,
            @PathVariable
            final String channelId,
            @RequestParam
            final String query) {
        log.info("Received request GET /api/discord/messages/{}/channel/{}/search with query:{}", botId, channelId, query);
        final List<ChannelMessageDto> result = dmService.searchGuildChannelMessages(botId, channelId, query);
        log.info("Produced response 200 for GET /api/discord/messages/{}/channel/{}/search", botId, channelId);
        return result;
    }

    @Operation(summary = "Delete private message")
    @DeleteMapping("/delete")
    public void deleteMessage(
            @PathVariable Long botId,
            @RequestParam List<Long> messageIds) {
        log.info("Received request DELETE /api/discord/messages/{}/delete with messageIds:{}", botId, messageIds);
        dmService.deleteMessages(botId, messageIds);
        log.info("Produced response 200 for DELETE /api/discord/messages/{}/delete", botId);
    }

    @Operation(summary = "Update private message")
    @PutMapping("/update/{messageId}")
    public void updateMessages(
            @PathVariable
            final Long botId,
            @PathVariable
            final Long messageId,
            @RequestParam
            final String updatedMessage) {
        log.info("Received request PUT /api/discord/messages/{}/update/{}", botId, messageId);
        dmService.updateMessage(botId, messageId, updatedMessage);
        log.info("Produced response 200 for PUT /api/discord/messages/{}/update/{}", botId, messageId);
    }

    @Operation(summary = "Delete guild channel message")
    @DeleteMapping("/channel/delete")
    public void deleteChannelMessages(
            @PathVariable
            final Long botId,
            @RequestParam
            final String channelId,
            @RequestParam
            final List<String> messageIds) {
        log.info("Received request DELETE /api/discord/messages/{}/channel/delete with channelId:{}, messageIds:{}", botId, channelId, messageIds);
        dmService.deleteChannelMessages(botId, channelId, messageIds);
        log.info("Produced response 200 for DELETE /api/discord/messages/{}/channel/delete", botId);
    }

    @Operation(summary = "Update guild channel message")
    @PutMapping("/update/channel/{messageId}")
    public void updateChannelMessages(
            @PathVariable
            final Long botId,
            @RequestParam
            final String channelId,
            @PathVariable
            final String messageId,
            @RequestParam
            final String updatedMessage) {
        log.info("Received request PUT /api/discord/messages/{}/update/channel/{} with channelId:{}", botId, messageId, channelId);
        dmService.updateChannelMessage(botId, channelId, messageId, updatedMessage);
        log.info("Produced response 200 for PUT /api/discord/messages/{}/update/channel/{}", botId, messageId);
    }
}
