package com.sdc.discord.controller;

import com.sdc.discord.domain.dto.chat.DiscordPrivateChatDto;
import com.sdc.discord.domain.dto.guild.GuildChannelDto;
import com.sdc.discord.domain.dto.guild.GuildDto;
import com.sdc.discord.domain.dto.guild.GuildUserDto;
import com.sdc.discord.service.DiscordChatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Discord chats controller.
 * @since 12.2025
 */
@Slf4j
@RestController
@RequestMapping("/api/discord/{botId}/chats")
@RequiredArgsConstructor
public class DiscordChatController {
    private final DiscordChatService chatService;

    @Operation(summary = "Get private chats")
    @GetMapping("/private")
    public List<DiscordPrivateChatDto> getChats(@PathVariable Long botId) {
        log.info("Received request GET /api/discord/{}/chats/private", botId);
        List<DiscordPrivateChatDto> chats = chatService.getChats(botId);
        log.info("Produced response 200 for GET /api/discord/{}/chats/private", botId);
        return chats;
    }

    @Operation(summary = "Get guilds")
    @GetMapping("/guilds")
    public List<GuildDto> getGuilds(@PathVariable final Long botId) {
        log.info("Received request GET /api/discord/{}/chats/guilds", botId);
        final List<GuildDto> result = chatService.getGuilds(botId);
        log.info("Produced response 200 for GET /api/discord/{}/chats/guilds", botId);
        return result;
    }

    @Operation(summary = "Get guild channels")
    @GetMapping("/guilds/{guildId}/channels")
    public List<GuildChannelDto> getChannels(
            @PathVariable
            final Long botId,
            @PathVariable
            final String guildId) {
        log.info("Received request GET /api/discord/{}/chats/guilds/{}/channels", botId, guildId);
        final List<GuildChannelDto> channels = chatService.getGuildChannels(botId, guildId);
        log.info("Produced response 200 for GET /api/discord/{}/chats/guilds/{}/channels", botId, guildId);
        return channels;
    }

    @Operation(summary = "Get available users")
    @GetMapping("/users")
    public List<GuildUserDto> getUsers(@PathVariable final Long botId) {
        log.info("Received request GET /api/discord/{}/chats/users", botId);
        final List<GuildUserDto> users = chatService.getAvailableUsers(botId);
        log.info("Produced response 200 for GET /api/discord/{}/chats/users", botId);
        return users;
    }

    @Operation(summary = "Search private chats")
    @GetMapping("/search")
    public List<DiscordPrivateChatDto> searchChats(
            @PathVariable
            final Long botId,
            @RequestParam
            final String query) {
        log.info("Received request GET /api/discord/{}/chats/search with query:{}", botId, query);
        final List<DiscordPrivateChatDto> chats = chatService.searchChats(botId, query);
        log.info("Produced response 200 for GET /api/discord/{}/chats/search", botId);
        return chats;
    }

    @Operation(summary = "Search guild channels")
    @GetMapping("/{guildId}/channels/search")
    public List<GuildChannelDto> searchChats(
            @PathVariable
            final Long botId,
            @PathVariable
            final String guildId,
            @RequestParam
            final String query) {
        log.info("Received request GET /api/discord/{}/chats/{}/channels/search with query:{}", botId, guildId, query);
        final List<GuildChannelDto> chats = chatService.searchGuildChannels(botId, guildId, query);
        log.info("Produced response 200 for GET /api/discord/{}/chats/{}/channels/search", botId, guildId);
        return chats;
    }

    @Operation(summary = "Get private chat")
    @GetMapping("/{channelId}")
    public DiscordPrivateChatDto getChat(
            @PathVariable
            final Long botId,
            @PathVariable
            final String channelId) {
        log.info("Received request GET /api/discord/{}/chats/{}", botId, channelId);
        final DiscordPrivateChatDto chat = chatService.getChat(botId, channelId);
        log.info("Produced response 200 for GET /api/discord/{}/chats/{}", botId, channelId);
        return chat;
    }
}
