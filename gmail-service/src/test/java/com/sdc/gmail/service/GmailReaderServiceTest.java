package com.sdc.gmail.service;

import com.sdc.gmail.domain.model.GmailCredentials;
import com.sdc.gmail.repository.GmailCredentialsRepository;
import com.sdc.gmail.utils.CryptoUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GmailReaderServiceTest {

    @Mock
    private GmailCredentialsRepository gmailCredentialsRepository;

    @Mock
    private CryptoUtils cryptoUtils;

    @InjectMocks
    private GmailReaderService gmailReaderService;

    @Test
    void testGetMessages_AccountNotFound() {
        when(gmailCredentialsRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            gmailReaderService.getMessages(1L, 20, null);
        });

        assertTrue(exception.getMessage().contains("Account not found"));
        verify(gmailCredentialsRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUnreadMessages_AccountNotFound() {
        when(gmailCredentialsRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            gmailReaderService.getUnreadMessages(1L, 20, null);
        });

        assertTrue(exception.getMessage().contains("Account not found"));
        verify(gmailCredentialsRepository, times(1)).findById(1L);
    }

    @Test
    void testSearchMessages_AccountNotFound() {
        when(gmailCredentialsRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            gmailReaderService.searchMessages(1L, "from:test@gmail.com", 20, null);
        });

        assertTrue(exception.getMessage().contains("Account not found"));
        verify(gmailCredentialsRepository, times(1)).findById(1L);
    }

    @Test
    void testMarkAsRead_AccountNotFound() {
        when(gmailCredentialsRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            gmailReaderService.markAsRead(1L, "msg123");
        });

        assertTrue(exception.getMessage().contains("Account not found"));
        verify(gmailCredentialsRepository, times(1)).findById(1L);
    }

    @Test
    void testMarkAsUnread_AccountNotFound() {
        when(gmailCredentialsRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            gmailReaderService.markAsUnread(1L, "msg123");
        });

        assertTrue(exception.getMessage().contains("Account not found"));
        verify(gmailCredentialsRepository, times(1)).findById(1L);
    }

    @Test
    void testDownloadImageStream_CreatesStreamingResponseBody() {
        final StreamingResponseBody result = gmailReaderService.downloadImageStream(1L, "msg123", "att123");

        assertNotNull(result);
    }

    @Test
    void testDownloadVideoStream_CreatesStreamingResponseBody() {
        final StreamingResponseBody result = gmailReaderService.downloadVideoStream(1L, "msg123", "att123");

        assertNotNull(result);
    }

    @Test
    void testDownloadDocumentStream_CreatesStreamingResponseBody() {
        final StreamingResponseBody result = gmailReaderService.downloadDocumentStream(1L, "msg123", "att123");

        assertNotNull(result);
    }

    @Test
    void testDownloadImageStream_ExecutionThrowsExceptionWhenAccountNotFound() {
        when(gmailCredentialsRepository.findById(1L)).thenReturn(Optional.empty());

        final StreamingResponseBody result = gmailReaderService.downloadImageStream(1L, "msg123", "att123");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        assertThrows(RuntimeException.class, () -> result.writeTo(outputStream));
        verify(gmailCredentialsRepository, times(1)).findById(1L);
    }

    @Test
    void testDownloadVideoStream_ExecutionThrowsExceptionWhenAccountNotFound() {
        when(gmailCredentialsRepository.findById(1L)).thenReturn(Optional.empty());

        final StreamingResponseBody result = gmailReaderService.downloadVideoStream(1L, "msg123", "att123");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        assertThrows(RuntimeException.class, () -> result.writeTo(outputStream));
        verify(gmailCredentialsRepository, times(1)).findById(1L);
    }

    @Test
    void testDownloadDocumentStream_ExecutionThrowsExceptionWhenAccountNotFound() {
        when(gmailCredentialsRepository.findById(1L)).thenReturn(Optional.empty());

        final StreamingResponseBody result = gmailReaderService.downloadDocumentStream(1L, "msg123", "att123");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        assertThrows(RuntimeException.class, () -> result.writeTo(outputStream));
        verify(gmailCredentialsRepository, times(1)).findById(1L);
    }

    @Test
    void testRepositoryFindById_Found() {
        final GmailCredentials credentials = new GmailCredentials();
        credentials.setId(1L);
        credentials.setEmailAddress("test@gmail.com");

        when(gmailCredentialsRepository.findById(1L)).thenReturn(Optional.of(credentials));

        final Optional<GmailCredentials> result = gmailCredentialsRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("test@gmail.com", result.get().getEmailAddress());
        verify(gmailCredentialsRepository, times(1)).findById(1L);
    }

    @Test
    void testRepositoryFindById_NotFound() {
        when(gmailCredentialsRepository.findById(999L)).thenReturn(Optional.empty());

        final Optional<GmailCredentials> result = gmailCredentialsRepository.findById(999L);

        assertFalse(result.isPresent());
        verify(gmailCredentialsRepository, times(1)).findById(999L);
    }

    @Test
    void testRepositorySave() {
        final GmailCredentials credentials = new GmailCredentials();
        credentials.setEmailAddress("test@gmail.com");

        when(gmailCredentialsRepository.save(any(GmailCredentials.class))).thenReturn(credentials);

        final GmailCredentials result = gmailCredentialsRepository.save(credentials);

        assertNotNull(result);
        assertEquals("test@gmail.com", result.getEmailAddress());
        verify(gmailCredentialsRepository, times(1)).save(credentials);
    }

    @Test
    void testCryptoUtilsDecryption() throws Exception {
        when(cryptoUtils.decrypt("encrypted_value")).thenReturn("decrypted_value");

        final String decrypted = cryptoUtils.decrypt("encrypted_value");

        assertEquals("decrypted_value", decrypted);
        verify(cryptoUtils, times(1)).decrypt("encrypted_value");
    }

    @Test
    void testCryptoUtilsEncryption() throws Exception {
        when(cryptoUtils.encrypt("plain_text")).thenReturn("encrypted_plain_text");

        final String encrypted = cryptoUtils.encrypt("plain_text");

        assertEquals("encrypted_plain_text", encrypted);
        verify(cryptoUtils, times(1)).encrypt("plain_text");
    }

    @Test
    void testTokenExpiry_ValidToken() {
        final GmailCredentials credentials = new GmailCredentials();
        credentials.setTokenExpiry(LocalDateTime.now().plusHours(2));

        assertTrue(credentials.getTokenExpiry().isAfter(LocalDateTime.now()));
    }

    @Test
    void testTokenExpiry_ExpiredToken() {
        final GmailCredentials credentials = new GmailCredentials();
        credentials.setTokenExpiry(LocalDateTime.now().minusHours(1));

        assertTrue(credentials.getTokenExpiry().isBefore(LocalDateTime.now()));
    }
}
