package com.sdc.main.service;

import com.sdc.main.domain.dto.discord.chat.DiscordPrivateChatDto;
import com.sdc.main.domain.dto.discord.guild.GuildChannelDto;
import com.sdc.main.domain.dto.discord.guild.GuildDto;
import com.sdc.main.domain.dto.discord.guild.GuildUserDto;
import com.sdc.main.domain.dto.telegram.TelegramChatFolderDto;
import com.sdc.main.domain.dto.telegram.TelegramNotificationDto;
import com.sdc.main.domain.dto.telegram.chat.ChatTdlibDto;
import com.sdc.main.integration.client.AIServiceClient;
import com.sdc.main.integration.client.DiscordServiceClient;
import com.sdc.main.integration.client.GmailServiceClient;
import com.sdc.main.integration.client.TelegramServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Chat-management service.
 * @since 12.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final TelegramServiceClient telegramClient;
    private final AIServiceClient aiClient;
    private final GmailServiceClient gmailClient;
    private final DiscordServiceClient discordClient;

    public List<ChatTdlibDto> findAllTelegramChatsMain(final int limit, final String accountId) {
        return telegramClient.findAllChatsMain(limit, accountId);
    }

    public List<ChatTdlibDto> findAllTelegramChatsArchive(final int limit, final String accountId) {
        return telegramClient.findAllChatsArchive(limit, accountId);
    }

    public List<TelegramChatFolderDto> findAllTelegramChatFolders( final String accountId) {
        return telegramClient.findAllChatFolders(accountId);
    }

    public List<ChatTdlibDto> findAllTelegramChatsFolder(final int limit, final int folderId, final String accountId) {
        return telegramClient.findAllChatsFolder(limit, folderId, accountId);
    }

    public Long getTelegramUserChatId(final String username, final String accountId) {
        return telegramClient.getUserChatId(username, accountId);
    }

    public Long createTelegramChat(final Long userId, final String accountId) {
        return telegramClient.createChat(userId, accountId);
    }

    public List<TelegramNotificationDto> findTelegramChatNotifications(final String accountId) {
        return telegramClient.findChatsNotifications(accountId);
    }

    public void createEmptyTelegramChat(final Long chatId, final String accountId) {
        telegramClient.createEmptyChat(chatId, accountId);
    }

    public void deleteEmptyTelegramChat(final Long chatId, final String accountId) {
        telegramClient.deleteEmptyChat(chatId, accountId);
    }

    public List<DiscordPrivateChatDto> getDiscordPrivateChats(final Long botId) {
        return discordClient.getPrivateChats(botId);
    }

    public List<GuildDto> getDiscordGuilds(final Long botId) {
        return discordClient.getGuilds(botId);
    }

    public List<GuildChannelDto> getDiscordGuildChannels(final Long botId, final Long guildId) {
        return discordClient.getChannels(botId, guildId);
    }

    public List<GuildUserDto> getDiscordUsers(final Long botId) {
        return discordClient.getUsers(botId);
    }

    public List<DiscordPrivateChatDto> searchDiscordPrivateChats(final Long botId, final String query) {
        return discordClient.searchChats(botId, query);
    }

    public List<GuildChannelDto> searchDiscordGuildChannels(final Long botId, final Long guildId, final String query) {
        return discordClient.searchGuildChannels(botId, guildId, query);
    }

    public DiscordPrivateChatDto getDiscordPrivateChat(final Long botId, final String channelId) {
        return discordClient.getChat(botId, channelId);
    }
 }
