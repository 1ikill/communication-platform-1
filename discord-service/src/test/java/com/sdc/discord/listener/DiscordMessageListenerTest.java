package com.sdc.discord.listener;

import com.sdc.discord.domain.model.DiscordCredentials;
import com.sdc.discord.domain.model.DiscordMessageFile;
import com.sdc.discord.domain.model.DiscordPrivateChat;
import com.sdc.discord.domain.model.DiscordPrivateMessage;
import com.sdc.discord.repository.DiscordCredentialsRepository;
import com.sdc.discord.repository.DiscordMessageFileRepository;
import com.sdc.discord.repository.DiscordPrivateChatRepository;
import com.sdc.discord.repository.DiscordPrivateMessageRepository;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscordMessageListenerTest {

    @Mock
    private DiscordCredentialsRepository credentialsRepository;
    @Mock
    private DiscordPrivateMessageRepository messageRepository;
    @Mock
    private DiscordPrivateChatRepository chatRepository;
    @Mock
    private DiscordMessageFileRepository fileRepository;
    @Mock
    private MessageReceivedEvent event;
    @Mock
    private JDA jda;
    @Mock
    private SelfUser selfUser;
    @Mock
    private User user;
    @Mock
    private Message message;
    @Mock
    private PrivateChannel privateChannel;
    @Mock
    private net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion messageChannelUnion;

    private DiscordMessageListener listener;
    private Map<Long, DiscordCredentials> botCache;

    @BeforeEach
    void setUp() throws Exception {
        listener = new DiscordMessageListener(
            credentialsRepository,
            messageRepository,
            chatRepository,
            fileRepository
        );
        
        // Access private botCache field
        Field field = DiscordMessageListener.class.getDeclaredField("botCache");
        field.setAccessible(true);
        botCache = (Map<Long, DiscordCredentials>) field.get(listener);
    }

    @Test
    void testRegisterBot() {
        DiscordCredentials bot = new DiscordCredentials();
        bot.setId(1L);
        bot.setBotUsername("TestBot");

        listener.registerBot(bot);

        assertEquals(1, botCache.size());
        assertTrue(botCache.containsKey(1L));
        assertEquals(bot, botCache.get(1L));
    }

    @Test
    void testRegisterMultipleBots() {
        DiscordCredentials bot1 = new DiscordCredentials();
        bot1.setId(1L);
        bot1.setBotUsername("TestBot1");

        DiscordCredentials bot2 = new DiscordCredentials();
        bot2.setId(2L);
        bot2.setBotUsername("TestBot2");

        listener.registerBot(bot1);
        listener.registerBot(bot2);

        assertEquals(2, botCache.size());
        assertTrue(botCache.containsKey(1L));
        assertTrue(botCache.containsKey(2L));
    }

    @Test
    void testUnregisterBot() {
        DiscordCredentials bot = new DiscordCredentials();
        bot.setId(1L);
        bot.setBotUsername("TestBot");

        listener.registerBot(bot);
        assertEquals(1, botCache.size());

        listener.unregisterBot(1L);

        assertEquals(0, botCache.size());
        assertFalse(botCache.containsKey(1L));
    }

    @Test
    void testUnregisterNonexistentBot() {
        listener.unregisterBot(999L);

        assertEquals(0, botCache.size());
    }

    @Test
    void testOnMessageReceivedIgnoresNonPrivateMessages() {
        when(event.isFromType(ChannelType.PRIVATE)).thenReturn(false);

        listener.onMessageReceived(event);

        verify(credentialsRepository, never()).findByBotUserId(anyString());
    }

    @Test
    void testOnMessageReceivedIgnoresWhenBotNotFound() {
        when(event.isFromType(ChannelType.PRIVATE)).thenReturn(true);
        when(event.getJDA()).thenReturn(jda);
        when(jda.getSelfUser()).thenReturn(selfUser);
        when(selfUser.getId()).thenReturn("bot123");
        when(credentialsRepository.findByBotUserId("bot123")).thenReturn(Optional.empty());

        listener.onMessageReceived(event);

        verify(event, never()).getMessage();
    }

    @Test
    void testRegisterBotReplacesExistingBot() {
        DiscordCredentials bot1 = new DiscordCredentials();
        bot1.setId(1L);
        bot1.setBotUsername("TestBot1");

        DiscordCredentials bot2 = new DiscordCredentials();
        bot2.setId(1L);
        bot2.setBotUsername("TestBot2");

        listener.registerBot(bot1);
        listener.registerBot(bot2);

        assertEquals(1, botCache.size());
        assertEquals("TestBot2", botCache.get(1L).getBotUsername());
    }

    @Test
    void testMultipleUnregisterSameBot() {
        DiscordCredentials bot = new DiscordCredentials();
        bot.setId(1L);
        bot.setBotUsername("TestBot");

        listener.registerBot(bot);
        listener.unregisterBot(1L);
        listener.unregisterBot(1L); // Second unregister should not throw

        assertEquals(0, botCache.size());
    }

    @Test
    void testRegisterAfterUnregister() {
        DiscordCredentials bot = new DiscordCredentials();
        bot.setId(1L);
        bot.setBotUsername("TestBot");

        listener.registerBot(bot);
        listener.unregisterBot(1L);
        listener.registerBot(bot);

        assertEquals(1, botCache.size());
        assertTrue(botCache.containsKey(1L));
    }
}
