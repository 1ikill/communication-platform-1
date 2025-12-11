package com.sdc.discord.controller;

import com.sdc.discord.service.DiscordFileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DiscordFileController
 */
@ExtendWith(MockitoExtension.class)
class DiscordFileControllerTest {

    @Mock
    private DiscordFileService discordFileService;

    @InjectMocks
    private DiscordFileController controller;

    @Test
    void testGetImageFromUrl() {
        String url = "https://cdn.discordapp.com/attachments/123/456/image.jpg";
        StreamingResponseBody mockBody = mock(StreamingResponseBody.class);

        when(discordFileService.getFileFromUrl(url)).thenReturn(mockBody);

        StreamingResponseBody result = controller.getImageFromUrl(url);

        assertNotNull(result);
        assertEquals(mockBody, result);
        verify(discordFileService, times(1)).getFileFromUrl(url);
    }

    @Test
    void testGetVideoFromUrl() {
        String url = "https://cdn.discordapp.com/attachments/123/456/video.mp4";
        StreamingResponseBody mockBody = mock(StreamingResponseBody.class);

        when(discordFileService.getFileFromUrl(url)).thenReturn(mockBody);

        StreamingResponseBody result = controller.getVideoFromUrl(url);

        assertNotNull(result);
        assertEquals(mockBody, result);
        verify(discordFileService, times(1)).getFileFromUrl(url);
    }

    @Test
    void testGetDocumentFromUrl() {
        String url = "https://cdn.discordapp.com/attachments/123/456/document.pdf";
        StreamingResponseBody mockBody = mock(StreamingResponseBody.class);

        when(discordFileService.getFileFromUrl(url)).thenReturn(mockBody);

        StreamingResponseBody result = controller.getDocumentFromUrl(url);

        assertNotNull(result);
        assertEquals(mockBody, result);
        verify(discordFileService, times(1)).getFileFromUrl(url);
    }

    @Test
    void testGetImageFromUrlWithQueryParams() {
        String url = "https://cdn.discordapp.com/attachments/123/456/image.jpg?size=1024";
        StreamingResponseBody mockBody = mock(StreamingResponseBody.class);

        when(discordFileService.getFileFromUrl(url)).thenReturn(mockBody);

        StreamingResponseBody result = controller.getImageFromUrl(url);

        assertNotNull(result);
        verify(discordFileService).getFileFromUrl(url);
    }

    @Test
    void testGetVideoFromUrlDifferentFormat() {
        String url = "https://cdn.discordapp.com/attachments/789/012/video.webm";
        StreamingResponseBody mockBody = mock(StreamingResponseBody.class);

        when(discordFileService.getFileFromUrl(url)).thenReturn(mockBody);

        StreamingResponseBody result = controller.getVideoFromUrl(url);

        assertNotNull(result);
        verify(discordFileService).getFileFromUrl(url);
    }

    @Test
    void testGetDocumentFromUrlDifferentFormat() {
        String url = "https://cdn.discordapp.com/attachments/345/678/file.docx";
        StreamingResponseBody mockBody = mock(StreamingResponseBody.class);

        when(discordFileService.getFileFromUrl(url)).thenReturn(mockBody);

        StreamingResponseBody result = controller.getDocumentFromUrl(url);

        assertNotNull(result);
        verify(discordFileService).getFileFromUrl(url);
    }
}
