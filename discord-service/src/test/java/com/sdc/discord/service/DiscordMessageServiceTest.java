package com.sdc.discord.service;

import com.sdc.discord.config.DiscordCredentialsManager;
import com.sdc.discord.config.security.CurrentUser;
import com.sdc.discord.domain.dto.message.ChannelMessageDto;
import com.sdc.discord.domain.dto.message.DiscordPrivateMessageDto;
import com.sdc.discord.domain.exception.BadRequestException;
import com.sdc.discord.domain.exception.NotFoundException;
import com.sdc.discord.domain.mapper.ChannelMessageMapper;
import com.sdc.discord.domain.mapper.DiscordPrivateMessageMapper;
import com.sdc.discord.domain.model.DiscordCredentials;
import com.sdc.discord.domain.model.DiscordPrivateMessage;
import com.sdc.discord.repository.DiscordCredentialsRepository;
import com.sdc.discord.repository.DiscordPrivateMessageRepository;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscordMessageServiceTest {

    @Mock
    private DiscordPrivateMessageRepository messageRepository;
    @Mock
    private DiscordCredentialsRepository credentialsRepository;
    @Mock
    private DiscordCredentialsManager credentialsManager;
    @Mock
    private DiscordPrivateMessageMapper mapper;
    @Mock
    private ChannelMessageMapper channelMessageMapper;
    @Mock
    private CurrentUser currentUser;
    @Mock
    private JDA jda;
    @Mock
    private User user;
    @Mock
    private TextChannel textChannel;
    @Mock
    private PrivateChannel privateChannel;

    @InjectMocks
    private DiscordMessageService service;

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

        lenient().when(currentUser.getId()).thenReturn(userId);
        lenient().when(currentUser.getUsername()).thenReturn("testuser");
    }

    @Test
    void testSendDirectMessageThrowsNotFoundWhenUserNotFound() {
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(jda);
        when(jda.getUserById("user123")).thenReturn(null);

        assertThrows(NotFoundException.class, 
            () -> service.sendDirectMessage(botId, "user123", "Hello"));
    }

    @Test
    void testSendDirectMessageThrowsBadRequestWhenTargetIsBot() {
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(jda);
        when(jda.getUserById("bot123")).thenReturn(user);
        when(user.isBot()).thenReturn(true);

        assertThrows(BadRequestException.class, 
            () -> service.sendDirectMessage(botId, "bot123", "Hello"));
    }

    @Test
    void testSendChannelMessageThrowsNotFoundWhenChannelNotFound() {
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(jda);
        when(jda.getTextChannelById("channel123")).thenReturn(null);

        assertThrows(NotFoundException.class, 
            () -> service.sendChannelMessage(botId, "channel123", "Hello"));
    }

    @Test
    void testSendChannelFileMessageThrowsNotFoundWhenChannelNotFound() {
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(jda);
        when(jda.getTextChannelById("channel123")).thenReturn(null);

        List<MultipartFile> files = new ArrayList<>();
        files.add(new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes()));

        assertThrows(NotFoundException.class, 
            () -> service.sendChannelFileMessage(botId, "channel123", files, "Hello"));
    }

    @Test
    void testSendDirectFileMessageThrowsNotFoundWhenUserNotFound() {
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(jda);
        when(jda.getUserById("user123")).thenReturn(null);

        List<MultipartFile> files = new ArrayList<>();
        files.add(new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes()));

        assertThrows(NotFoundException.class, 
            () -> service.sendDirectFileMessage(botId, "user123", files, "Hello"));
    }

    @Test
    void testGetGuildChannelHistoryThrowsBadRequestWhenBotNotBelongsToUser() {
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(false);

        assertThrows(BadRequestException.class, 
            () -> service.getGuildChannelHistory(botId, "channel123", 50));
    }

    @Test
    void testGetGuildChannelHistoryThrowsNotFoundWhenBotNotFound() {
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, 
            () -> service.getGuildChannelHistory(botId, "channel123", 50));
    }

    @Test
    void testGetGuildChannelHistoryThrowsRuntimeExceptionWhenJdaIsNull() {
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(null);

        assertThrows(RuntimeException.class, 
            () -> service.getGuildChannelHistory(botId, "channel123", 50));
    }

    @Test
    void testGetGuildChannelHistoryThrowsNotFoundWhenChannelNotFound() {
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(jda);
        when(jda.getTextChannelById("channel123")).thenReturn(null);

        assertThrows(NotFoundException.class, 
            () -> service.getGuildChannelHistory(botId, "channel123", 50));
    }

    @Test
    void testGetGuildChannelHistory() {
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(jda);
        when(jda.getTextChannelById("channel123")).thenReturn(textChannel);

        MessageHistory messageHistory = mock(MessageHistory.class);
        when(textChannel.getHistory()).thenReturn(messageHistory);

        @SuppressWarnings("unchecked")
        RestAction<List<Message>> restAction = mock(RestAction.class);
        when(messageHistory.retrievePast(50)).thenReturn(restAction);

        Message message = mock(Message.class);
        User author = mock(User.class);
        when(message.getAuthor()).thenReturn(author);
        when(author.getId()).thenReturn("botUserId");
        when(message.getTimeCreated()).thenReturn(OffsetDateTime.now());
        when(message.getAttachments()).thenReturn(new ArrayList<>());

        List<Message> messages = List.of(message);
        when(restAction.complete()).thenReturn(messages);

        ChannelMessageDto dto = new ChannelMessageDto("msgId", "author", "authorId", "content", null, false, true, new ArrayList<>());
        when(channelMessageMapper.toDto(any(), any(), anyBoolean(), anyList())).thenReturn(dto);

        List<ChannelMessageDto> result = service.getGuildChannelHistory(botId, "channel123", 50);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(channelMessageMapper, times(1)).toDto(any(), any(), anyBoolean(), anyList());
    }



    @Test
    void testDeleteChannelMessagesThrowsNotFoundWhenBotNotFound() {
        when(credentialsRepository.findById(botId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, 
            () -> service.deleteChannelMessages(botId, "channel123", List.of("msg1")));
    }

    @Test
    void testDeleteChannelMessagesThrowsRuntimeExceptionWhenJdaIsNull() {
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(null);

        assertThrows(RuntimeException.class, 
            () -> service.deleteChannelMessages(botId, "channel123", List.of("msg1")));
    }

    @Test
    void testDeleteChannelMessagesThrowsNotFoundWhenChannelNotFound() {
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(jda);
        when(jda.getTextChannelById("channel123")).thenReturn(null);

        assertThrows(NotFoundException.class, 
            () -> service.deleteChannelMessages(botId, "channel123", List.of("msg1")));
    }

    @Test
    void testGetGuildChannelHistoryWithMultipleMessages() {
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(jda);
        when(jda.getTextChannelById("channel123")).thenReturn(textChannel);

        MessageHistory messageHistory = mock(MessageHistory.class);
        when(textChannel.getHistory()).thenReturn(messageHistory);

        @SuppressWarnings("unchecked")
        RestAction<List<Message>> restAction = mock(RestAction.class);
        when(messageHistory.retrievePast(50)).thenReturn(restAction);

        Message message1 = mock(Message.class);
        Message message2 = mock(Message.class);
        User author1 = mock(User.class);
        User author2 = mock(User.class);
        
        when(message1.getAuthor()).thenReturn(author1);
        when(message2.getAuthor()).thenReturn(author2);
        when(author1.getId()).thenReturn("botUserId");
        when(author2.getId()).thenReturn("otherUserId");
        when(message1.getTimeCreated()).thenReturn(OffsetDateTime.now());
        when(message2.getTimeCreated()).thenReturn(OffsetDateTime.now());
        when(message1.getAttachments()).thenReturn(new ArrayList<>());
        when(message2.getAttachments()).thenReturn(new ArrayList<>());

        List<Message> messages = List.of(message1, message2);
        when(restAction.complete()).thenReturn(messages);

        ChannelMessageDto dto1 = new ChannelMessageDto("msg1", "author1", "botUserId", "content1", null, false, true, new ArrayList<>());
        ChannelMessageDto dto2 = new ChannelMessageDto("msg2", "author2", "otherUserId", "content2", null, false, false, new ArrayList<>());
        
        when(channelMessageMapper.toDto(eq(message1), any(), eq(true), anyList())).thenReturn(dto1);
        when(channelMessageMapper.toDto(eq(message2), any(), eq(false), anyList())).thenReturn(dto2);

        List<ChannelMessageDto> result = service.getGuildChannelHistory(botId, "channel123", 50);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(channelMessageMapper, times(2)).toDto(any(), any(), anyBoolean(), anyList());
    }

    @Test
    void testUpdateChannelMessageWithEmptyContent() {
        assertThrows(IllegalArgumentException.class,
            () -> service.updateChannelMessage(botId, "channel123", "msg123", ""));
    }

    @Test
    void testUpdateChannelMessageWithNullContent() {
        assertThrows(IllegalArgumentException.class,
            () -> service.updateChannelMessage(botId, "channel123", "msg123", null));
    }

    @Test
    void testUpdateChannelMessageThrowsBadRequestForInvalidBot() {
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(false);

        assertThrows(BadRequestException.class,
            () -> service.updateChannelMessage(botId, "channel123", "msg123", "Updated"));
    }

    @Test
    void testUpdateChannelMessageThrowsNotFoundForMissingChannel() {
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(jda);
        when(jda.getTextChannelById("channel123")).thenReturn(null);

        assertThrows(NotFoundException.class,
            () -> service.updateChannelMessage(botId, "channel123", "msg123", "Updated"));
    }

    @Test
    void testGetPrivateMessageHistorySuccess() {
        DiscordPrivateMessage msg1 = new DiscordPrivateMessage();
        msg1.setId(1L);
        msg1.setContent("Hello");

        when(messageRepository.findLastMessages(botId, "channel123", 50))
            .thenReturn(List.of(msg1));
        
        DiscordPrivateMessageDto dto = mock(DiscordPrivateMessageDto.class);
        lenient().when(dto.getId()).thenReturn(1L);
        lenient().when(dto.getContent()).thenReturn("Hello");
        
        when(mapper.toDto(msg1)).thenReturn(dto);

        List<DiscordPrivateMessageDto> result = service.getPrivateMessageHistory(botId, "channel123", 50);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(messageRepository).findLastMessages(botId, "channel123", 50);
    }

    @Test
    void testGetPrivateMessageHistoryEmpty() {
        when(messageRepository.findLastMessages(botId, "channel123", 50))
            .thenReturn(List.of());

        List<DiscordPrivateMessageDto> result = service.getPrivateMessageHistory(botId, "channel123", 50);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchMessagesSuccess() {
        DiscordPrivateMessage msg1 = new DiscordPrivateMessage();
        msg1.setId(1L);
        msg1.setContent("Hello world");

        when(messageRepository.searchByContentNative(botId, "channel123", "Hello"))
            .thenReturn(List.of(msg1));
        
        DiscordPrivateMessageDto dto = mock(DiscordPrivateMessageDto.class);
        lenient().when(dto.getId()).thenReturn(1L);
        lenient().when(dto.getContent()).thenReturn("Hello world");
        
        when(mapper.toDto(msg1)).thenReturn(dto);

        List<DiscordPrivateMessageDto> result = service.searchMessages(botId, "channel123", "Hello");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testSearchGuildChannelMessagesSuccess() {
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(jda);
        when(jda.getTextChannelById("channel123")).thenReturn(textChannel);

        MessageHistory messageHistory = mock(MessageHistory.class);
        when(textChannel.getHistory()).thenReturn(messageHistory);

        @SuppressWarnings("unchecked")
        RestAction<List<Message>> restAction = mock(RestAction.class);
        when(messageHistory.retrievePast(100)).thenReturn(restAction);

        Message message = mock(Message.class);
        User author = mock(User.class);
        when(message.getAuthor()).thenReturn(author);
        when(author.getId()).thenReturn("botUserId");
        lenient().when(message.getContentDisplay()).thenReturn("Hello world");
        when(message.getTimeCreated()).thenReturn(OffsetDateTime.now());
        when(message.getAttachments()).thenReturn(new ArrayList<>());

        List<Message> messages = List.of(message);
        when(restAction.complete()).thenReturn(messages);

        ChannelMessageDto dto = new ChannelMessageDto("msg1", "author1", "botUserId", "Hello world", null, false, true, new ArrayList<>());
        lenient().when(channelMessageMapper.toDto(eq(message), any(), eq(true), anyList())).thenReturn(dto);

        List<ChannelMessageDto> result = service.searchGuildChannelMessages(botId, "channel123", "Hello");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testSearchGuildChannelMessagesEmptyResult() {
        when(credentialsRepository.existsByUserIdAndId(userId, botId)).thenReturn(true);
        when(credentialsRepository.findById(botId)).thenReturn(Optional.of(testBot));
        when(credentialsManager.getJda(testBot)).thenReturn(jda);
        when(jda.getTextChannelById("channel123")).thenReturn(textChannel);

        MessageHistory messageHistory = mock(MessageHistory.class);
        when(textChannel.getHistory()).thenReturn(messageHistory);

        @SuppressWarnings("unchecked")
        RestAction<List<Message>> restAction = mock(RestAction.class);
        when(messageHistory.retrievePast(100)).thenReturn(restAction);

        Message message = mock(Message.class);
        User author = mock(User.class);
        when(message.getAuthor()).thenReturn(author);
        when(author.getId()).thenReturn("botUserId");
        when(message.getTimeCreated()).thenReturn(OffsetDateTime.now());
        when(message.getAttachments()).thenReturn(new ArrayList<>());

        List<Message> messages = List.of(message);
        when(restAction.complete()).thenReturn(messages);

        ChannelMessageDto dto = new ChannelMessageDto("msg1", "author1", "botUserId", "Some other text", null, false, true, new ArrayList<>());
        when(channelMessageMapper.toDto(eq(message), any(), eq(true), anyList())).thenReturn(dto);

        List<ChannelMessageDto> result = service.searchGuildChannelMessages(botId, "channel123", "Hello");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
