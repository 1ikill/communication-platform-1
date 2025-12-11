package com.sdc.discord.config;

import com.sdc.discord.domain.model.DiscordCredentials;
import com.sdc.discord.listener.DiscordMessageListener;
import com.sdc.discord.repository.DiscordCredentialsRepository;
import com.sdc.discord.utils.CryptoUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscordCredentialsManagerTest {

    @Mock
    private DiscordCredentialsRepository credentialsRepository;
    @Mock
    private CryptoUtils cryptoUtils;
    @Mock
    private DiscordMessageListener messageListener;
    @Mock
    private JDA jda;
    @Mock
    private Guild guild;

    private DiscordCredentialsManager manager;
    private Map<String, JDA> activeBots;

    @BeforeEach
    void setUp() throws Exception {
        manager = new DiscordCredentialsManager(credentialsRepository, cryptoUtils, messageListener);
        
        // Access private field using reflection
        Field field = DiscordCredentialsManager.class.getDeclaredField("activeBots");
        field.setAccessible(true);
        activeBots = (Map<String, JDA>) field.get(manager);
    }

    @Test
    void testInitAllBotsWhenNoActiveBots() {
        when(credentialsRepository.findAllByIsActive(true)).thenReturn(new ArrayList<>());

        manager.initAllBots();

        verify(credentialsRepository, times(1)).findAllByIsActive(true);
        assertTrue(activeBots.isEmpty());
    }

    @Test
    void testIsBotConnectedReturnsTrueWhenConnected() {
        activeBots.put("bot123", jda);
        when(jda.getStatus()).thenReturn(JDA.Status.CONNECTED);

        boolean result = manager.isBotConnected("bot123");

        assertTrue(result);
    }

    @Test
    void testIsBotConnectedReturnsFalseWhenNotConnected() {
        activeBots.put("bot123", jda);
        when(jda.getStatus()).thenReturn(JDA.Status.DISCONNECTED);

        boolean result = manager.isBotConnected("bot123");

        assertFalse(result);
    }

    @Test
    void testIsBotConnectedReturnsFalseWhenBotNotExists() {
        boolean result = manager.isBotConnected("nonexistent");

        assertFalse(result);
    }

    @Test
    void testGetBotStatsWithNoBots() {
        Map<String, Object> stats = manager.getBotStats();

        assertNotNull(stats);
        assertEquals(0, stats.get("totalBots"));
        assertTrue(((List<?>) stats.get("bots")).isEmpty());
    }

    @Test
    void testGetBotStatsWithOneBotConnected() {
        activeBots.put("bot123", jda);
        when(jda.getStatus()).thenReturn(JDA.Status.CONNECTED);
        when(jda.getGuilds()).thenReturn(List.of(guild, guild, guild));
        when(jda.getGatewayPing()).thenReturn(50L);

        Map<String, Object> stats = manager.getBotStats();

        assertNotNull(stats);
        assertEquals(1, stats.get("totalBots"));
        
        List<Map<String, Object>> botDetails = (List<Map<String, Object>>) stats.get("bots");
        assertEquals(1, botDetails.size());
        
        Map<String, Object> botDetail = botDetails.get(0);
        assertEquals("bot123", botDetail.get("botUserId"));
        assertEquals("CONNECTED", botDetail.get("status"));
        assertEquals(3, botDetail.get("guildCount"));
        assertEquals(50L, botDetail.get("ping"));
    }

    @Test
    void testGetBotStatsWithMultipleBots() {
        JDA jda2 = mock(JDA.class);
        
        activeBots.put("bot1", jda);
        activeBots.put("bot2", jda2);
        
        when(jda.getStatus()).thenReturn(JDA.Status.CONNECTED);
        when(jda.getGuilds()).thenReturn(List.of(guild));
        when(jda.getGatewayPing()).thenReturn(30L);
        
        when(jda2.getStatus()).thenReturn(JDA.Status.RECONNECT_QUEUED);
        when(jda2.getGuilds()).thenReturn(List.of(guild, guild));
        when(jda2.getGatewayPing()).thenReturn(100L);

        Map<String, Object> stats = manager.getBotStats();

        assertEquals(2, stats.get("totalBots"));
        List<Map<String, Object>> botDetails = (List<Map<String, Object>>) stats.get("bots");
        assertEquals(2, botDetails.size());
    }

    @Test
    void testShutdownAllBots() {
        JDA jda2 = mock(JDA.class);
        activeBots.put("bot1", jda);
        activeBots.put("bot2", jda2);

        manager.shutdownAllBots();

        verify(jda, times(1)).shutdown();
        verify(jda2, times(1)).shutdown();
        assertTrue(activeBots.isEmpty());
    }

    @Test
    void testShutdownAllBotsWithException() {
        activeBots.put("bot1", jda);
        doThrow(new RuntimeException("Shutdown error")).when(jda).shutdown();

        // Should not throw, just log error
        assertDoesNotThrow(() -> manager.shutdownAllBots());
        assertTrue(activeBots.isEmpty());
    }

    @Test
    void testGetJdaReturnsExistingConnectedBot() {
        DiscordCredentials bot = new DiscordCredentials();
        bot.setBotUserId("bot123");
        bot.setBotUsername("TestBot");
        
        activeBots.put("bot123", jda);
        when(jda.getStatus()).thenReturn(JDA.Status.CONNECTED);

        JDA result = manager.getJda(bot);

        assertNotNull(result);
        assertEquals(jda, result);
        verify(jda, never()).shutdown();
    }
}
