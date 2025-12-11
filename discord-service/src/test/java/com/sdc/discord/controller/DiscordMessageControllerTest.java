package com.sdc.discord.controller;

import com.sdc.discord.domain.dto.message.ChannelMessageDto;
import com.sdc.discord.domain.dto.message.DiscordMessageFileDto;
import com.sdc.discord.domain.dto.message.DiscordPrivateMessageDto;
import com.sdc.discord.service.DiscordMessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DiscordMessageController
 */
@ExtendWith(MockitoExtension.class)
class DiscordMessageControllerTest {

    @Mock
    private DiscordMessageService dmService;

    @InjectMocks
    private DiscordMessageController controller;

    @Test
    void testSendDirectMessage() {
        Long botId = 1L;
        String userId = "user123";
        String message = "Hello!";

        doNothing().when(dmService).sendDirectMessage(botId, userId, message);

        controller.sendDirectMessage(botId, userId, message);

        verify(dmService, times(1)).sendDirectMessage(botId, userId, message);
    }

    @Test
    void testSendChannelMessage() {
        Long botId = 1L;
        String channelId = "channel123";
        String message = "Hello channel!";

        doNothing().when(dmService).sendChannelMessage(botId, channelId, message);

        controller.sendChannelMessage(botId, channelId, message);

        verify(dmService, times(1)).sendChannelMessage(botId, channelId, message);
    }

    @Test
    void testSendChannelFileMessage() throws IOException {
        Long botId = 1L;
        String channelId = "channel123";
        MultipartFile file = mock(MultipartFile.class);
        List<MultipartFile> files = List.of(file);
        String message = "File attached";

        doNothing().when(dmService).sendChannelFileMessage(botId, channelId, files, message);

        controller.sendChannelFileMessage(botId, channelId, files, message);

        verify(dmService, times(1)).sendChannelFileMessage(botId, channelId, files, message);
    }

    @Test
    void testSendChannelFileMessageWithoutText() throws IOException {
        Long botId = 1L;
        String channelId = "channel123";
        MultipartFile file = mock(MultipartFile.class);
        List<MultipartFile> files = List.of(file);

        doNothing().when(dmService).sendChannelFileMessage(botId, channelId, files, null);

        controller.sendChannelFileMessage(botId, channelId, files, null);

        verify(dmService, times(1)).sendChannelFileMessage(botId, channelId, files, null);
    }

    @Test
    void testSendDmFileMessage() throws IOException {
        Long botId = 1L;
        String userId = "user123";
        MultipartFile file = mock(MultipartFile.class);
        List<MultipartFile> files = List.of(file);
        String message = "DM file";

        doNothing().when(dmService).sendDirectFileMessage(botId, userId, files, message);

        controller.sendDmFileMessage(botId, userId, files, message);

        verify(dmService, times(1)).sendDirectFileMessage(botId, userId, files, message);
    }

    @Test
    void testGetGuildChannelHistory() {
        Long botId = 1L;
        String channelId = "channel123";
        int limit = 50;
        
        ChannelMessageDto msg1 = new ChannelMessageDto("msg1", "User1", "user1", "Hello", 
                LocalDateTime.now(), false, false, List.of());
        ChannelMessageDto msg2 = new ChannelMessageDto("msg2", "User2", "user2", "Hi", 
                LocalDateTime.now(), false, false, List.of());
        List<ChannelMessageDto> expectedMessages = Arrays.asList(msg1, msg2);

        when(dmService.getGuildChannelHistory(botId, channelId, limit)).thenReturn(expectedMessages);

        List<ChannelMessageDto> result = controller.getGuildChannelHistory(botId, channelId, limit);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Hello", result.get(0).getContent());
        assertEquals("Hi", result.get(1).getContent());
        verify(dmService, times(1)).getGuildChannelHistory(botId, channelId, limit);
    }

    @Test
    void testGetGuildChannelHistoryWithDefaultLimit() {
        Long botId = 1L;
        String channelId = "channel123";
        int defaultLimit = 50;
        
        when(dmService.getGuildChannelHistory(botId, channelId, defaultLimit)).thenReturn(List.of());

        List<ChannelMessageDto> result = controller.getGuildChannelHistory(botId, channelId, defaultLimit);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(dmService).getGuildChannelHistory(botId, channelId, defaultLimit);
    }

    @Test
    void testDeleteMessage() {
        Long botId = 1L;
        List<Long> messageIds = Arrays.asList(1L, 2L, 3L);

        doNothing().when(dmService).deleteMessages(botId, messageIds);

        controller.deleteMessage(botId, messageIds);

        verify(dmService, times(1)).deleteMessages(botId, messageIds);
    }

    @Test
    void testDeleteChannelMessages() {
        Long botId = 1L;
        String channelId = "channel123";
        List<String> messageIds = Arrays.asList("msg1", "msg2");

        doNothing().when(dmService).deleteChannelMessages(botId, channelId, messageIds);

        controller.deleteChannelMessages(botId, channelId, messageIds);

        verify(dmService, times(1)).deleteChannelMessages(botId, channelId, messageIds);
    }

    @Test
    void testSendDirectMessageWithEmptyMessage() {
        Long botId = 1L;
        String userId = "user123";
        String message = "";

        doNothing().when(dmService).sendDirectMessage(botId, userId, message);

        controller.sendDirectMessage(botId, userId, message);

        verify(dmService).sendDirectMessage(botId, userId, message);
    }

    @Test
    void testSendChannelMessageWithLongText() {
        Long botId = 1L;
        String channelId = "channel123";
        String message = "A".repeat(2000);

        doNothing().when(dmService).sendChannelMessage(botId, channelId, message);

        controller.sendChannelMessage(botId, channelId, message);

        verify(dmService).sendChannelMessage(botId, channelId, message);
    }
}
