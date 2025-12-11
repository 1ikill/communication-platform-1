package com.sdc.discord.service;

import com.sdc.discord.config.DiscordCredentialsManager;
import com.sdc.discord.config.security.CurrentUser;
import com.sdc.discord.domain.dto.chat.DiscordPrivateChatDto;
import com.sdc.discord.domain.dto.guild.GuildChannelDto;
import com.sdc.discord.domain.dto.guild.GuildDto;
import com.sdc.discord.domain.dto.guild.GuildUserDto;
import com.sdc.discord.domain.exception.BadRequestException;
import com.sdc.discord.domain.exception.NotFoundException;
import com.sdc.discord.domain.mapper.DiscordPrivateChatMapper;
import com.sdc.discord.domain.mapper.GuildChannelMapper;
import com.sdc.discord.domain.mapper.GuildMapper;
import com.sdc.discord.domain.mapper.GuildUserMapper;
import com.sdc.discord.domain.model.DiscordCredentials;
import com.sdc.discord.domain.model.DiscordPrivateChat;
import com.sdc.discord.repository.DiscordCredentialsRepository;
import com.sdc.discord.repository.DiscordPrivateChatRepository;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DiscordChatService
 */
@ExtendWith(MockitoExtension.class)
class DiscordChatServiceTest {

    @Mock
    private DiscordPrivateChatRepository chatRepository;

    @Mock
    private DiscordCredentialsRepository credentialsRepository;

    @Mock
    private DiscordCredentialsManager credentialsManager;

    @Mock
    private DiscordPrivateChatMapper privateChatMapper;

    @Mock
    private GuildMapper guildMapper;

    @Mock
    private GuildChannelMapper guildChannelMapper;

    @Mock
    private GuildUserMapper guildUserMapper;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private DiscordChatService service;

    @Mock
    private JDA jda;

    @Mock
    private Guild guild;

    @Mock
    private User user;

    @Mock
    private Member member;

    @Mock
    private SelfUser selfUser;

    @Mock
    private TextChannel textChannel;

    private DiscordCredentials testBot;
    private Long botId = 1L;
    private Long userId = 100L;

    @BeforeEach
    void setUp() {
        testBot = new DiscordCredentials();
        testBot.setId(botId);
        testBot.setBotToken("encrypted_token");
        testBot.setBotUserId("botUserId");
        testBot.setUserId(userId);
        testBot.setIsActive(true);

        when(currentUser.getId()).thenReturn(userId);
        lenient().when(currentUser.getUsername()).thenReturn("testuser");
    }

    @Test
    void testGetGuilds() {
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(jda);

        Guild guild1 = mock(Guild.class);
        Guild guild2 = mock(Guild.class);
        when(jda.getGuilds()).thenReturn(Arrays.asList(guild1, guild2));

        GuildDto dto1 = new GuildDto("1", "Guild 1", "icon1", 100);
        GuildDto dto2 = new GuildDto("2", "Guild 2", "icon2", 200);
        when(guildMapper.toDto(guild1)).thenReturn(dto1);
        when(guildMapper.toDto(guild2)).thenReturn(dto2);

        List<GuildDto> result = service.getGuilds(botId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Guild 1", result.get(0).getName());
        assertEquals("Guild 2", result.get(1).getName());
        verify(jda).getGuilds();
        verify(guildMapper, times(2)).toDto(any(Guild.class));
    }

    @Test
    void testGetGuildsThrowsExceptionWhenBotNotFound() {
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> service.getGuilds(botId));
        verify(credentialsManager, never()).getJda(any());
    }

    @Test
    void testGetGuildsThrowsExceptionWhenJdaIsNull() {
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> service.getGuilds(botId));
    }

    @Test
    void testGetGuildChannels() {
        String guildId = "guild123";
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(jda);
        when(jda.getGuildById(guildId)).thenReturn(guild);

        TextChannel channel1 = mock(TextChannel.class);
        TextChannel channel2 = mock(TextChannel.class);
        when(guild.getTextChannels()).thenReturn(Arrays.asList(channel1, channel2));

        GuildChannelDto dto1 = new GuildChannelDto("ch1", "general", "topic1", 0);
        GuildChannelDto dto2 = new GuildChannelDto("ch2", "random", "topic2", 1);
        when(guildChannelMapper.toDto(channel1)).thenReturn(dto1);
        when(guildChannelMapper.toDto(channel2)).thenReturn(dto2);

        List<GuildChannelDto> result = service.getGuildChannels(botId, guildId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("general", result.get(0).getName());
        verify(guild).getTextChannels();
    }

    @Test
    void testGetGuildChannelsThrowsNotFoundWhenGuildNotFound() {
        String guildId = "nonexistent";
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(jda);
        when(jda.getGuildById(guildId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> service.getGuildChannels(botId, guildId));
    }

    @Test
    void testGetChats() {
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(jda);

        DiscordPrivateChat chat1 = new DiscordPrivateChat();
        chat1.setId(1L);
        chat1.setBotId(botId);
        chat1.setChannelId("ch1");
        chat1.setUserId("user1");
        chat1.setUserName("User One");
        chat1.setMessageCount(5);

        DiscordPrivateChat chat2 = new DiscordPrivateChat();
        chat2.setId(2L);
        chat2.setBotId(botId);
        chat2.setChannelId("ch2");
        chat2.setUserId("user2");
        chat2.setUserName("User Two");
        chat2.setMessageCount(10);

        List<DiscordPrivateChat> chats = Arrays.asList(chat1, chat2);
        when(chatRepository.findByBotIdOrderByLastMessageTimeDesc(botId)).thenReturn(chats);

        User mockUser1 = mock(User.class);
        User mockUser2 = mock(User.class);
        when(mockUser1.getName()).thenReturn("User One");
        when(mockUser1.getEffectiveAvatarUrl()).thenReturn("avatar1");
        when(mockUser2.getName()).thenReturn("User Two");
        when(mockUser2.getEffectiveAvatarUrl()).thenReturn("avatar2");

        net.dv8tion.jda.api.requests.restaction.CacheRestAction<User> restAction1 = mock(net.dv8tion.jda.api.requests.restaction.CacheRestAction.class);
        net.dv8tion.jda.api.requests.restaction.CacheRestAction<User> restAction2 = mock(net.dv8tion.jda.api.requests.restaction.CacheRestAction.class);
        when(jda.retrieveUserById("user1")).thenReturn(restAction1);
        when(jda.retrieveUserById("user2")).thenReturn(restAction2);
        when(restAction1.complete()).thenReturn(mockUser1);
        when(restAction2.complete()).thenReturn(mockUser2);

        when(chatRepository.saveAll(anyList())).thenReturn(chats);

        DiscordPrivateChatDto dto1 = new DiscordPrivateChatDto(1L, botId, "ch1", "user1", "User One",
                "avatar1", LocalDateTime.now(), "msg1", 5, LocalDateTime.now(), LocalDateTime.now());
        DiscordPrivateChatDto dto2 = new DiscordPrivateChatDto(2L, botId, "ch2", "user2", "User Two",
                "avatar2", LocalDateTime.now(), "msg2", 10, LocalDateTime.now(), LocalDateTime.now());

        when(privateChatMapper.toDto(chat1)).thenReturn(dto1);
        when(privateChatMapper.toDto(chat2)).thenReturn(dto2);

        List<DiscordPrivateChatDto> result = service.getChats(botId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(chatRepository).findByBotIdOrderByLastMessageTimeDesc(botId);
        verify(chatRepository).saveAll(anyList());
    }

    @Test
    void testSearchChats() {
        String query = "test";
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(jda);

        DiscordPrivateChat chat = new DiscordPrivateChat();
        chat.setId(1L);
        chat.setBotId(botId);
        chat.setChannelId("ch1");
        chat.setUserId("user1");
        chat.setUserName("Test User");

        when(chatRepository.searchByUserName(botId, query)).thenReturn(List.of(chat));

        User mockUser = mock(User.class);
        when(mockUser.getName()).thenReturn("Test User");
        when(mockUser.getEffectiveAvatarUrl()).thenReturn("avatar");

        net.dv8tion.jda.api.requests.restaction.CacheRestAction<User> restAction = mock(net.dv8tion.jda.api.requests.restaction.CacheRestAction.class);
        when(jda.retrieveUserById("user1")).thenReturn(restAction);
        when(restAction.complete()).thenReturn(mockUser);

        when(chatRepository.saveAll(anyList())).thenReturn(List.of(chat));

        DiscordPrivateChatDto dto = new DiscordPrivateChatDto(1L, botId, "ch1", "user1", "Test User",
                "avatar", LocalDateTime.now(), "msg1", 0, LocalDateTime.now(), LocalDateTime.now());
        when(privateChatMapper.toDto(chat)).thenReturn(dto);

        List<DiscordPrivateChatDto> result = service.searchChats(botId, query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test User", result.get(0).getUserName());
        verify(chatRepository).searchByUserName(botId, query);
    }

    @Test
    void testSearchGuildChannels() {
        String guildId = "guild123";
        String query = "gen";
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(jda);
        when(jda.getGuildById(guildId)).thenReturn(guild);

        TextChannel channel1 = mock(TextChannel.class);
        TextChannel channel2 = mock(TextChannel.class);
        when(guild.getTextChannels()).thenReturn(Arrays.asList(channel1, channel2));

        GuildChannelDto dto1 = new GuildChannelDto("ch1", "general", "topic", 0);
        GuildChannelDto dto2 = new GuildChannelDto("ch2", "random", "topic", 1);
        when(guildChannelMapper.toDto(channel1)).thenReturn(dto1);
        when(guildChannelMapper.toDto(channel2)).thenReturn(dto2);

        List<GuildChannelDto> result = service.searchGuildChannels(botId, guildId, query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("general", result.get(0).getName());
    }

    @Test
    void testGetChat() {
        String channelId = "ch123";
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(jda);

        DiscordPrivateChat chat = new DiscordPrivateChat();
        chat.setId(1L);
        chat.setBotId(botId);
        chat.setChannelId(channelId);
        chat.setUserId("user1");
        chat.setUserName("User");

        when(chatRepository.findByBotIdAndChannelId(botId, channelId)).thenReturn(Optional.of(chat));

        User mockUser = mock(User.class);
        when(mockUser.getName()).thenReturn("User");
        when(mockUser.getEffectiveAvatarUrl()).thenReturn("avatar");

        net.dv8tion.jda.api.requests.restaction.CacheRestAction<User> restAction = mock(net.dv8tion.jda.api.requests.restaction.CacheRestAction.class);
        when(jda.retrieveUserById("user1")).thenReturn(restAction);
        when(restAction.complete()).thenReturn(mockUser);

        DiscordPrivateChatDto dto = new DiscordPrivateChatDto(1L, botId, channelId, "user1", "User",
                "avatar", LocalDateTime.now(), "msg1", 0, LocalDateTime.now(), LocalDateTime.now());
        when(privateChatMapper.toDto(chat)).thenReturn(dto);

        DiscordPrivateChatDto result = service.getChat(botId, channelId);

        assertNotNull(result);
        assertEquals(channelId, result.getChannelId());
        verify(chatRepository).findByBotIdAndChannelId(botId, channelId);
    }
}
