package com.sdc.discord.service;

import com.sdc.discord.config.DiscordCredentialsManager;
import com.sdc.discord.config.security.CurrentUser;
import com.sdc.discord.domain.dto.bot.DiscordBotInfoDto;
import com.sdc.discord.domain.dto.request.AddBotRequestDto;
import com.sdc.discord.domain.mapper.DiscordCredentialsMapper;
import com.sdc.discord.domain.model.DiscordCredentials;
import com.sdc.discord.repository.DiscordCredentialsRepository;
import com.sdc.discord.utils.CryptoUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link DiscordService}
 */
@ExtendWith(MockitoExtension.class)
class DiscordServiceTest {

    @Mock
    private DiscordCredentialsRepository credentialsRepository;

    @Mock
    private CryptoUtils cryptoUtils;

    @Mock
    private CurrentUser currentUser;

    @Mock
    private DiscordCredentialsManager credentialsManager;

    @Mock
    private DiscordCredentialsMapper credentialsMapper;

    @InjectMocks
    private DiscordService discordService;

    private DiscordCredentials testCredentials;
    private DiscordBotInfoDto testBotInfoDto;

    @BeforeEach
    void setUp() {
        testCredentials = new DiscordCredentials();
        testCredentials.setId(1L);
        testCredentials.setUserId(100L);
        testCredentials.setBotToken("encrypted-token");
        testCredentials.setBotUserId("bot123");
        testCredentials.setBotUsername("TestBot");
        testCredentials.setIsActive(true);
        testCredentials.setCreatedDate(LocalDateTime.now());

        testBotInfoDto = new DiscordBotInfoDto(1L, 100L, "bot123", "TestBot", true, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @DisplayName("Should get connected bots for current user")
    void testGetConnectedBots() {
        Long userId = 100L;
        List<DiscordCredentials> credentials = Arrays.asList(testCredentials);

        when(currentUser.getId()).thenReturn(userId);
        when(credentialsRepository.findAllByUserIdAndIsActive(userId, true)).thenReturn(credentials);
        when(credentialsMapper.toDto(testCredentials)).thenReturn(testBotInfoDto);

        List<DiscordBotInfoDto> result = discordService.getConnectedBots();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBotInfoDto, result.get(0));

        verify(currentUser).getId();
        verify(credentialsRepository).findAllByUserIdAndIsActive(userId, true);
        verify(credentialsMapper).toDto(testCredentials);
    }

    @Test
    @DisplayName("Should return empty list when no connected bots")
    void testGetConnectedBotsEmpty() {
        Long userId = 100L;

        when(currentUser.getId()).thenReturn(userId);
        when(credentialsRepository.findAllByUserIdAndIsActive(userId, true)).thenReturn(List.of());

        List<DiscordBotInfoDto> result = discordService.getConnectedBots();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(currentUser).getId();
        verify(credentialsRepository).findAllByUserIdAndIsActive(userId, true);
        verify(credentialsMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should filter only active bots")
    void testGetConnectedBotsOnlyActive() {
        Long userId = 100L;
        DiscordCredentials activeBot = new DiscordCredentials();
        activeBot.setId(1L);
        activeBot.setIsActive(true);
        activeBot.setBotUsername("ActiveBot");

        when(currentUser.getId()).thenReturn(userId);
        when(credentialsRepository.findAllByUserIdAndIsActive(userId, true))
            .thenReturn(Arrays.asList(activeBot));
        when(credentialsMapper.toDto(activeBot)).thenReturn(testBotInfoDto);

        List<DiscordBotInfoDto> result = discordService.getConnectedBots();

        assertEquals(1, result.size());
        verify(credentialsRepository).findAllByUserIdAndIsActive(userId, true);
    }

    @Test
    @DisplayName("Should map multiple bots correctly")
    void testGetConnectedBotsMultiple() {
        Long userId = 100L;
        LocalDateTime now = LocalDateTime.now();
        DiscordCredentials cred1 = new DiscordCredentials();
        cred1.setId(1L);
        cred1.setBotUsername("Bot1");

        DiscordCredentials cred2 = new DiscordCredentials();
        cred2.setId(2L);
        cred2.setBotUsername("Bot2");

        DiscordBotInfoDto dto1 = new DiscordBotInfoDto(1L, userId, "bot1", "Bot1", true, now, now);
        DiscordBotInfoDto dto2 = new DiscordBotInfoDto(2L, userId, "bot2", "Bot2", true, now, now);

        when(currentUser.getId()).thenReturn(userId);
        when(credentialsRepository.findAllByUserIdAndIsActive(userId, true))
            .thenReturn(Arrays.asList(cred1, cred2));
        when(credentialsMapper.toDto(cred1)).thenReturn(dto1);
        when(credentialsMapper.toDto(cred2)).thenReturn(dto2);

        List<DiscordBotInfoDto> result = discordService.getConnectedBots();

        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }
}
