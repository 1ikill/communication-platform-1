package com.sdc.main.controller;

import com.sdc.main.domain.dto.discord.chat.DiscordPrivateChatDto;
import com.sdc.main.domain.dto.discord.guild.GuildChannelDto;
import com.sdc.main.domain.dto.discord.guild.GuildDto;
import com.sdc.main.domain.dto.discord.guild.GuildUserDto;
import com.sdc.main.domain.dto.telegram.TelegramChatFolderDto;
import com.sdc.main.domain.dto.telegram.TelegramNotificationDto;
import com.sdc.main.domain.dto.telegram.chat.ChatTdlibDto;
import com.sdc.main.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Chat-management controller.
 * @since 12.2025
 */
@Slf4j
@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @Operation(summary = "Get all chats in Telegram main chat list")
    @GetMapping("/telegram/main")
    public List<ChatTdlibDto> findAllTelegramChatsMain(
            @RequestParam
            final int limit,
            @RequestParam
            final String accountId) {
        log.info("Received request GET /chats/telegram/main with limit:{}, accountId:{}", limit, accountId);
        final List<ChatTdlibDto> result = chatService.findAllTelegramChatsMain(limit, accountId);
        log.info("Produced response 200 for GET /chats/telegram/main with body:{}", result);
        return result;
    }

    @Operation(summary = "Get all chats in Telegram archive chat list")
    @GetMapping("/telegram/archive")
    public List<ChatTdlibDto> findAllTelegramChatsArchive(
            @RequestParam
            final int limit,
            @RequestParam
            final String accountId) {
        log.info("Received request GET /chats/telegram/archive with limit:{}, accountId:{}", limit, accountId);
        final List<ChatTdlibDto> result = chatService.findAllTelegramChatsArchive(limit, accountId);
        log.info("Produced response 200 for GET /chats/telegram/archive with body:{}", result);
        return result;
    }

    @Operation(summary = "Get all Telegram chat folders")
    @GetMapping("/telegram/folders")
    public List<TelegramChatFolderDto> findAllTelegramChatFolders(
            @RequestParam
            final String accountId) {
        log.info("Received request GET /chats/telegram/folders with accountId:{}", accountId);
        final List<TelegramChatFolderDto> result = chatService.findAllTelegramChatFolders(accountId);
        log.info("Produced response 200 for GET /chats/telegram/folders with body:{}", result);
        return result;
    }


    @Operation(summary = "Get all chats in Telegram folder")
    @GetMapping("/telegram/folder")
    public List<ChatTdlibDto> findAllTelegramChatsFolder(
            @RequestParam
            final int limit,
            @RequestParam
            final int folderId,
            @RequestParam
            final String accountId) {
        log.info("Received request GET /chats/telegram/folder with limit:{}, folderId:{}, accountId:{}", limit, folderId, accountId);
        final List<ChatTdlibDto> result = chatService.findAllTelegramChatsFolder(limit, folderId, accountId);
        log.info("Produced response 200 for GET /chats/telegram/folder with body:{}", result);
        return result;
    }

    @Operation(summary = "Get telegram user chatId")
    @GetMapping("/telegram/user/chat")
    public Long getTelegramUserChatId(
            @RequestParam
            final String username,
            @RequestParam
            final String accountId) {
        log.info("Received request GET /chats/telegram/user/chat with username:{}, accountId:{}", username, accountId);
        final Long result = chatService.getTelegramUserChatId(username, accountId);
        log.info("Produced response 200 for GET /chats/telegram/user/chat with body:{}", result);
        return result;
    }

    @Operation(summary = "Create telegram chat with user")
    @PostMapping("/telegram/create")
    public Long createTelegramChat(
            @RequestParam
            final Long userId,
            @RequestParam
            final String accountId) {
        log.info("Received request POST /chats/telegram/create with userId:{}, accountId:{}", userId, accountId);
        final Long result = chatService.createTelegramChat(userId, accountId);
        log.info("Produced response 200 for POST /chats/telegram/create with body:{}", result);
        return result;
    }

    @Operation(summary = "Find telegram chats notifications")
    @GetMapping("/telegram/notifications")
    public List<TelegramNotificationDto> findTelegramChatNotifications(
            @RequestParam
            final String accountId) {
        log.info("Received request GET /chats/telegram/notifications with accountId:{}", accountId);
        final List<TelegramNotificationDto> result = chatService.findTelegramChatNotifications(accountId);
        log.info("Produced response 200 for GET /chats/telegram/notifications with body:{}", result);
        return result;
    }

    @Operation(summary = "Create empty telegram chat")
    @PostMapping("/telegram/empty/create")
    public void createEmptyTelegramChat(
            @RequestParam
            final Long chatId,
            @RequestParam
            final String accountId) {
        log.info("Received request POST /chats/telegram/empty/notifications with chatId:{}, accountId:{}", chatId, accountId);
        chatService.createEmptyTelegramChat(chatId, accountId);
        log.info("Produced response 200 for POST /chats/telegram/empty/notifications");
    }

    @Operation(summary = "Delete empty telegram chat")
    @DeleteMapping("/telegram/empty/delete")
    public void deleteEmptyTelegramChat(
            @RequestParam
            final Long chatId,
            @RequestParam
            final String accountId) {
        log.info("Received request DELETE /chats/telegram/empty/delete with chatId:{}, accountId:{}", chatId, accountId);
        chatService.deleteEmptyTelegramChat(chatId, accountId);
        log.info("Produced response 200 for DELETE /chats/telegram/empty/delete");
    }

    @Operation(summary = "Get discord private chats list")
    @GetMapping("/discord/dm")
    public List<DiscordPrivateChatDto> getDiscordPrivateChats(
            @RequestParam
            final Long botId) {
        log.info("Received request GET /chats/discord/dm with botId:{}", botId);
        final List<DiscordPrivateChatDto> result = chatService.getDiscordPrivateChats(botId);
        log.info("Produced response 200 for GET /chats/discord/dm with body:{}", result);
        return result;
    }

    @Operation(summary = "Get discord guilds")
    @GetMapping("/discord/guilds")
    public List<GuildDto> getDiscordGuilds(
            @RequestParam
            final Long botId) {
        log.info("Received request GET /chats/discord/guilds with botId:{}", botId);
        final List<GuildDto> result = chatService.getDiscordGuilds(botId);
        log.info("Produced response 200 for GET /chats/discord/guilds with body:{}", result);
        return result;
    }

    @Operation(summary = "Get discord guild channels")
    @GetMapping("/discord/guild/channels")
    public List<GuildChannelDto> getDiscordGuildChannels(
            @RequestParam
            final Long botId,
            @RequestParam
            final Long guildId) {
        log.info("Received request GET /chats/discord/guild/channels with botId:{}, guildId:{}", botId, guildId);
        final List<GuildChannelDto> result = chatService.getDiscordGuildChannels(botId, guildId);
        log.info("Produced response 200 for GET /chats/discord/guild/channels with body:{}", result);
        return result;
    }

    @Operation(summary = "Get available discord users")
    @GetMapping("/discord/users")
    public List<GuildUserDto> getDiscordUsers(
            @RequestParam
            final Long botId) {
        log.info("Received request GET /chats/discord/users with botId:{}", botId);
        final List<GuildUserDto> result = chatService.getDiscordUsers(botId);
        log.info("Produced response 200 for GET /chats/discord/users with body:{}", result);
        return result;
    }

    @Operation(summary = "Search discord private chats")
    @GetMapping("/discord/dm/search")
    public List<DiscordPrivateChatDto> searchDiscordPrivateChats(
            @RequestParam
            final Long botId,
            @RequestParam
            final String query) {
        log.info("Received request GET /chats/discord/dm/search with botId:{}, query:{}", botId, query);
        final List<DiscordPrivateChatDto> result = chatService.searchDiscordPrivateChats(botId, query);
        log.info("Produced response 200 for GET /chats/discord/dm/search with body:{}", result);
        return result;
    }

    @Operation(summary = "Search discord guild channels")
    @GetMapping("/discord/channels/search")
    public List<GuildChannelDto> searchDiscordGuildChannels(
            @RequestParam
            final Long botId,
            @RequestParam
            final Long guildId,
            @RequestParam
            final String query) {
        log.info("Received request GET /chats/discord/channels/search botId:{}, guildId:{}, query:{}", botId, guildId, query);
        final List<GuildChannelDto> result = chatService.searchDiscordGuildChannels(botId, guildId, query);
        log.info("Produced response 200 for GET /chats/discord/channels/search body:{}", result);
        return result;
    }

    @Operation(summary = "Get discord private chat")
    @GetMapping("/discord/dm/{channelId}")
    public DiscordPrivateChatDto getDiscordPrivateChat(
            @RequestParam
            final Long botId,
            @PathVariable
            final String channelId) {
        log.info("Received request GET /chats/discord/dm/{} with botId:{}", channelId, botId);
        final DiscordPrivateChatDto result = chatService.getDiscordPrivateChat(botId, channelId);
        log.info("Produced response 200 for GET /chats/discord/dm/{} with body:{}", channelId, result);
        return result;
    }
}
