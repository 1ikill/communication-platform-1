package com.sdc.gmail.controller;

import com.sdc.gmail.domain.dto.GmailMessagesResponseDto;
import com.sdc.gmail.service.GmailReaderService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GmailReaderControllerTest {

    @Mock
    private GmailReaderService gmailReaderService;

    @Mock
    private HttpServletResponse httpServletResponse;

    @InjectMocks
    private GmailReaderController gmailReaderController;

    private GmailMessagesResponseDto mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = new GmailMessagesResponseDto();
        mockResponse.setResultSizeEstimate(10L);
    }

    @Test
    void testGetMessages() throws Exception {
        final Long accountId = 1L;
        final Integer maxResults = 20;
        final String pageToken = "token123";
        
        when(gmailReaderService.getMessages(anyLong(), anyInt(), anyString()))
                .thenReturn(mockResponse);
        
        final GmailMessagesResponseDto result = gmailReaderController.getMessages(accountId, maxResults, pageToken);
        
        assertNotNull(result);
        assertEquals(10L, result.getResultSizeEstimate());
        verify(gmailReaderService, times(1)).getMessages(accountId, maxResults, pageToken);
    }

    @Test
    void testGetMessagesWithDefaultValues() throws Exception {
        final Long accountId = 1L;
        
        when(gmailReaderService.getMessages(anyLong(), anyInt(), isNull()))
                .thenReturn(mockResponse);
        
        final GmailMessagesResponseDto result = gmailReaderController.getMessages(accountId, 20, null);
        
        assertNotNull(result);
        verify(gmailReaderService, times(1)).getMessages(accountId, 20, null);
    }

    @Test
    void testGetMessagesThrowsException() throws Exception {
        final Long accountId = 1L;
        
        when(gmailReaderService.getMessages(anyLong(), anyInt(), isNull()))
                .thenThrow(new RuntimeException("Failed to get messages"));
        
        assertThrows(Exception.class, () -> gmailReaderController.getMessages(accountId, 20, null));
    }

    @Test
    void testGetUnreadMessages() throws Exception {
        final Long accountId = 1L;
        final Integer maxResults = 20;
        final String pageToken = "token123";
        
        when(gmailReaderService.getUnreadMessages(anyLong(), anyInt(), anyString()))
                .thenReturn(mockResponse);
        
        final GmailMessagesResponseDto result = gmailReaderController.getUnreadMessages(accountId, maxResults, pageToken);
        
        assertNotNull(result);
        assertEquals(10L, result.getResultSizeEstimate());
        verify(gmailReaderService, times(1)).getUnreadMessages(accountId, maxResults, pageToken);
    }

    @Test
    void testMarkAsRead() throws Exception {
        final Long accountId = 1L;
        final String messageId = "msg123";
        
        doNothing().when(gmailReaderService).markAsRead(anyLong(), anyString());
        
        gmailReaderController.markAsRead(accountId, messageId);
        
        verify(gmailReaderService, times(1)).markAsRead(accountId, messageId);
    }

    @Test
    void testMarkAsReadThrowsException() throws Exception {
        final Long accountId = 1L;
        final String messageId = "msg123";
        
        doThrow(new RuntimeException("Failed to mark as read"))
                .when(gmailReaderService).markAsRead(anyLong(), anyString());
        
        assertThrows(Exception.class, () -> gmailReaderController.markAsRead(accountId, messageId));
    }

    @Test
    void testMarkAsUnread() throws Exception {
        final Long accountId = 1L;
        final String messageId = "msg123";
        
        doNothing().when(gmailReaderService).markAsUnread(anyLong(), anyString());
        
        gmailReaderController.markAsUnread(accountId, messageId);
        
        verify(gmailReaderService, times(1)).markAsUnread(accountId, messageId);
    }

    @Test
    void testSearchMessages() throws Exception {
        final Long accountId = 1L;
        final String query = "from:test@gmail.com";
        final Integer maxResults = 20;
        final String pageToken = "token123";
        
        when(gmailReaderService.searchMessages(anyLong(), anyString(), anyInt(), anyString()))
                .thenReturn(mockResponse);
        
        final GmailMessagesResponseDto result = gmailReaderController.searchMessages(accountId, query, maxResults, pageToken);
        
        assertNotNull(result);
        assertEquals(10L, result.getResultSizeEstimate());
        verify(gmailReaderService, times(1)).searchMessages(accountId, query, maxResults, pageToken);
    }

    @Test
    void testDownloadImage() {
        final Long accountId = 1L;
        final String messageId = "msg123";
        final String attachmentId = "att123";
        final StreamingResponseBody mockStream = mock(StreamingResponseBody.class);
        
        when(gmailReaderService.downloadImageStream(anyLong(), anyString(), anyString()))
                .thenReturn(mockStream);
        
        final StreamingResponseBody result = gmailReaderController.downloadImage(accountId, messageId, attachmentId);
        
        assertNotNull(result);
        verify(gmailReaderService, times(1)).downloadImageStream(accountId, messageId, attachmentId);
    }

    @Test
    void testDownloadImageWithDifferentIds() {
        final Long accountId = 2L;
        final String messageId = "msg456";
        final String attachmentId = "att456";
        final StreamingResponseBody mockStream = mock(StreamingResponseBody.class);
        
        when(gmailReaderService.downloadImageStream(anyLong(), anyString(), anyString()))
                .thenReturn(mockStream);
        
        final StreamingResponseBody result = gmailReaderController.downloadImage(accountId, messageId, attachmentId);
        
        assertNotNull(result);
        verify(gmailReaderService, times(1)).downloadImageStream(accountId, messageId, attachmentId);
    }

    @Test
    void testDownloadVideo() {
        final Long accountId = 1L;
        final String messageId = "msg123";
        final String attachmentId = "att123";
        final StreamingResponseBody mockStream = mock(StreamingResponseBody.class);
        
        when(gmailReaderService.downloadVideoStream(anyLong(), anyString(), anyString()))
                .thenReturn(mockStream);
        
        final StreamingResponseBody result = gmailReaderController.downloadVideo(accountId, messageId, attachmentId);
        
        assertNotNull(result);
        verify(gmailReaderService, times(1)).downloadVideoStream(accountId, messageId, attachmentId);
    }

    @Test
    void testDownloadDocument() {
        final Long accountId = 1L;
        final String messageId = "msg123";
        final String attachmentId = "att123";
        final StreamingResponseBody mockStream = mock(StreamingResponseBody.class);
        
        when(gmailReaderService.downloadDocumentStream(anyLong(), anyString(), anyString()))
                .thenReturn(mockStream);
        
        final StreamingResponseBody result = gmailReaderController.downloadDocument(accountId, messageId, attachmentId);
        
        assertNotNull(result);
        verify(gmailReaderService, times(1)).downloadDocumentStream(accountId, messageId, attachmentId);
    }
}
