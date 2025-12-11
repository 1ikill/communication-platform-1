package com.sdc.discord.controller;

import com.sdc.discord.domain.dto.bot.DiscordBotInfoDto;
import com.sdc.discord.domain.dto.request.AddBotRequestDto;
import com.sdc.discord.service.DiscordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DiscordController
 */
@ExtendWith(MockitoExtension.class)
class DiscordControllerTest {

    @Mock
    private DiscordService discordService;

    @InjectMocks
    private DiscordController controller;

    @Test
    void testGetConnectedBots() {
        DiscordBotInfoDto bot1 = new DiscordBotInfoDto(1L, 100L, "123", "Bot1", true, null, null);
        DiscordBotInfoDto bot2 = new DiscordBotInfoDto(2L, 100L, "456", "Bot2", false, null, null);
        List<DiscordBotInfoDto> expectedBots = Arrays.asList(bot1, bot2);

        when(discordService.getConnectedBots()).thenReturn(expectedBots);

        List<DiscordBotInfoDto> result = controller.getConnectedBots();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Bot1", result.get(0).getBotUsername());
        assertEquals("Bot2", result.get(1).getBotUsername());
        verify(discordService, times(1)).getConnectedBots();
    }

    @Test
    void testGetConnectedBotsEmpty() {
        when(discordService.getConnectedBots()).thenReturn(List.of());

        List<DiscordBotInfoDto> result = controller.getConnectedBots();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(discordService, times(1)).getConnectedBots();
    }

    @Test
    void testAddBot() {
        AddBotRequestDto request = new AddBotRequestDto();
        request.setToken("token123");
        DiscordBotInfoDto expectedBot = new DiscordBotInfoDto(1L, 100L, "789", "NewBot", true, null, null);

        when(discordService.addBot(any(AddBotRequestDto.class))).thenReturn(expectedBot);

        DiscordBotInfoDto result = controller.addBot(request);

        assertNotNull(result);
        assertEquals("NewBot", result.getBotUsername());
        assertEquals(1L, result.getId());
        assertTrue(result.getIsActive());
        verify(discordService, times(1)).addBot(request);
    }

    @Test
    void testAddBotWithValidToken() {
        AddBotRequestDto request = new AddBotRequestDto();
        request.setToken("valid.token.here");
        DiscordBotInfoDto expectedBot = new DiscordBotInfoDto(5L, 100L, "999", "ValidBot", true, null, null);

        when(discordService.addBot(request)).thenReturn(expectedBot);

        DiscordBotInfoDto result = controller.addBot(request);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("ValidBot", result.getBotUsername());
        verify(discordService).addBot(request);
    }
}
