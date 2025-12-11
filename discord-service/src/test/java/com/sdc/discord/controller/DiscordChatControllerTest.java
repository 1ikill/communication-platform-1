package com.sdc.discord.controller;

import com.sdc.discord.domain.dto.chat.DiscordPrivateChatDto;
import com.sdc.discord.domain.dto.guild.GuildChannelDto;
import com.sdc.discord.domain.dto.guild.GuildDto;
import com.sdc.discord.domain.dto.guild.GuildUserDto;
import com.sdc.discord.service.DiscordChatService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DiscordChatController
 */
@ExtendWith(MockitoExtension.class)
class DiscordChatControllerTest {

    @Mock
    private DiscordChatService chatService;

    @InjectMocks
    private DiscordChatController controller;

    @Test
    void testGetChats() {
        Long botId = 1L;
        DiscordPrivateChatDto chat1 = new DiscordPrivateChatDto(1L, botId, "ch1", "user1", "User One", 
                "avatar1.png", LocalDateTime.now(), "msg1", 5, LocalDateTime.now(), LocalDateTime.now());
        DiscordPrivateChatDto chat2 = new DiscordPrivateChatDto(2L, botId, "ch2", "user2", "User Two", 
                "avatar2.png", LocalDateTime.now(), "msg2", 10, LocalDateTime.now(), LocalDateTime.now());
        List<DiscordPrivateChatDto> expectedChats = Arrays.asList(chat1, chat2);

        when(chatService.getChats(botId)).thenReturn(expectedChats);

        List<DiscordPrivateChatDto> result = controller.getChats(botId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("User One", result.get(0).getUserName());
        assertEquals("User Two", result.get(1).getUserName());
        verify(chatService, times(1)).getChats(botId);
    }

    @Test
    void testGetGuilds() {
        Long botId = 1L;
        GuildDto guild1 = new GuildDto("guild1", "Guild One", "icon1.png", 100);
        GuildDto guild2 = new GuildDto("guild2", "Guild Two", "icon2.png", 200);
        List<GuildDto> expectedGuilds = Arrays.asList(guild1, guild2);

        when(chatService.getGuilds(botId)).thenReturn(expectedGuilds);

        List<GuildDto> result = controller.getGuilds(botId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Guild One", result.get(0).getName());
        assertEquals(100, result.get(0).getMemberCount());
        verify(chatService, times(1)).getGuilds(botId);
    }

    @Test
    void testGetChannels() {
        Long botId = 1L;
        String guildId = "guild123";
        GuildChannelDto channel1 = new GuildChannelDto("ch1", "general", "Main channel", 0);
        GuildChannelDto channel2 = new GuildChannelDto("ch2", "random", "Random chat", 1);
        List<GuildChannelDto> expectedChannels = Arrays.asList(channel1, channel2);

        when(chatService.getGuildChannels(botId, guildId)).thenReturn(expectedChannels);

        List<GuildChannelDto> result = controller.getChannels(botId, guildId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("general", result.get(0).getName());
        assertEquals("random", result.get(1).getName());
        verify(chatService, times(1)).getGuildChannels(botId, guildId);
    }

    @Test
    void testGetUsers() {
        Long botId = 1L;
        GuildUserDto user1 = new GuildUserDto("user1", "alice", "Alice", "avatar1.png", "Guild One", false);
        GuildUserDto user2 = new GuildUserDto("user2", "bob", "Bob", "avatar2.png", "Guild One", false);
        List<GuildUserDto> expectedUsers = Arrays.asList(user1, user2);

        when(chatService.getAvailableUsers(botId)).thenReturn(expectedUsers);

        List<GuildUserDto> result = controller.getUsers(botId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("alice", result.get(0).getUsername());
        assertEquals("bob", result.get(1).getUsername());
        verify(chatService, times(1)).getAvailableUsers(botId);
    }

    @Test
    void testSearchChats() {
        Long botId = 1L;
        String query = "test";
        DiscordPrivateChatDto chat = new DiscordPrivateChatDto(1L, botId, "ch1", "user1", "Test User", 
                "avatar.png", LocalDateTime.now(), "msg1", 3, LocalDateTime.now(), LocalDateTime.now());
        List<DiscordPrivateChatDto> expectedChats = List.of(chat);

        when(chatService.searchChats(botId, query)).thenReturn(expectedChats);

        List<DiscordPrivateChatDto> result = controller.searchChats(botId, query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test User", result.get(0).getUserName());
        verify(chatService, times(1)).searchChats(botId, query);
    }

    @Test
    void testSearchGuildChannels() {
        Long botId = 1L;
        String guildId = "guild123";
        String query = "general";
        GuildChannelDto channel = new GuildChannelDto("ch1", "general", "General channel", 0);
        List<GuildChannelDto> expectedChannels = List.of(channel);

        when(chatService.searchGuildChannels(botId, guildId, query)).thenReturn(expectedChannels);

        List<GuildChannelDto> result = controller.searchChats(botId, guildId, query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("general", result.get(0).getName());
        verify(chatService, times(1)).searchGuildChannels(botId, guildId, query);
    }

    @Test
    void testGetChat() {
        Long botId = 1L;
        String channelId = "ch123";
        DiscordPrivateChatDto expectedChat = new DiscordPrivateChatDto(1L, botId, channelId, "user1", 
                "User", "avatar.png", LocalDateTime.now(), "msg1", 5, LocalDateTime.now(), LocalDateTime.now());

        when(chatService.getChat(botId, channelId)).thenReturn(expectedChat);

        DiscordPrivateChatDto result = controller.getChat(botId, channelId);

        assertNotNull(result);
        assertEquals(channelId, result.getChannelId());
        assertEquals("User", result.getUserName());
        verify(chatService, times(1)).getChat(botId, channelId);
    }

    @Test
    void testGetChatsEmpty() {
        Long botId = 1L;
        when(chatService.getChats(botId)).thenReturn(List.of());

        List<DiscordPrivateChatDto> result = controller.getChats(botId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(chatService).getChats(botId);
    }

    @Test
    void testGetGuildsEmpty() {
        Long botId = 1L;
        when(chatService.getGuilds(botId)).thenReturn(List.of());

        List<GuildDto> result = controller.getGuilds(botId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(chatService).getGuilds(botId);
    }
}
