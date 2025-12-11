package com.sdc.gmail.service;

import com.sdc.gmail.config.security.CurrentUser;
import com.sdc.gmail.domain.dto.GmailAccountInfoDto;
import com.sdc.gmail.domain.model.GmailCredentials;
import com.sdc.gmail.repository.GmailCredentialsRepository;
import com.sdc.gmail.utils.CryptoUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GmailCredentialsServiceTest {

    @Mock
    private GmailCredentialsRepository gmailCredentialsRepository;

    @Mock
    private CurrentUser currentUser;

    @Mock
    private CryptoUtils cryptoUtils;

    @InjectMocks
    private GmailCredentialsService gmailCredentialsService;

    @Test
    void testBuildAuthorizationUrl_Success() throws Exception {
        when(currentUser.getId()).thenReturn(100L);
        when(cryptoUtils.encrypt(anyString())).thenReturn("encrypted_value");

        final GmailCredentials credentials = new GmailCredentials();
        credentials.setId(1L);
        credentials.setUserId(100L);
        when(gmailCredentialsRepository.save(any(GmailCredentials.class))).thenReturn(credentials);

        final String clientSecretJson = """
                {
                  "web": {
                    "client_id": "test_client_id",
                    "client_secret": "test_client_secret",
                    "redirect_uris": ["http://localhost:8086/gmail/oauth/google/callback"]
                  }
                }
                """;

        final MultipartFile file = new MockMultipartFile(
                "file",
                "client_secret.json",
                "application/json",
                clientSecretJson.getBytes()
        );

        final String url = gmailCredentialsService.buildAuthorizationUrl(file);

        assertNotNull(url);
        assertTrue(url.contains("client_id"));
        assertTrue(url.contains("redirect_uri"));
        assertTrue(url.contains("scope"));
        assertTrue(url.contains("access_type=offline"));
        assertTrue(url.contains("prompt=consent"));
        assertTrue(url.contains("state"));
        verify(gmailCredentialsRepository, times(1)).save(any(GmailCredentials.class));
        verify(cryptoUtils, times(2)).encrypt(anyString());
    }

    @Test
    void testBuildAuthorizationUrl_WithInstalledType() throws Exception {
        when(currentUser.getId()).thenReturn(100L);
        when(cryptoUtils.encrypt(anyString())).thenReturn("encrypted_value");

        final GmailCredentials credentials = new GmailCredentials();
        credentials.setId(1L);
        credentials.setUserId(100L);
        when(gmailCredentialsRepository.save(any(GmailCredentials.class))).thenReturn(credentials);

        final String installedJson = """
                {
                  "installed": {
                    "client_id": "test_client_id_installed",
                    "client_secret": "test_client_secret_installed"
                  }
                }
                """;

        final MultipartFile file = new MockMultipartFile(
                "file",
                "client_secret.json",
                "application/json",
                installedJson.getBytes()
        );

        final String url = gmailCredentialsService.buildAuthorizationUrl(file);

        assertNotNull(url);
        assertTrue(url.contains("client_id"));
        verify(gmailCredentialsRepository, times(1)).save(any(GmailCredentials.class));
    }

    @Test
    void testBuildAuthorizationUrl_InvalidJson() {
        final MultipartFile file = new MockMultipartFile(
                "file",
                "client_secret.json",
                "application/json",
                "invalid json".getBytes()
        );

        assertThrows(Exception.class, () -> {
            gmailCredentialsService.buildAuthorizationUrl(file);
        });
    }

    @Test
    void testHandleOAuthCallback_AccountNotFound() {
        final String code = "auth_code_123";
        final String state = "userId=100&accountId=999";

        when(gmailCredentialsRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            gmailCredentialsService.handleOAuthCallback(code, state);
        });

        assertTrue(exception.getMessage().contains("Failed to find account"));
        verify(gmailCredentialsRepository, times(1)).findById(999L);
    }

    @Test
    void testHandleOAuthCallback_InvalidState() {
        final String code = "auth_code_123";
        final String state = "invalid_state_format";

        assertThrows(Exception.class, () -> {
            gmailCredentialsService.handleOAuthCallback(code, state);
        });
    }

    @Test
    void testGetMe_Success() {
        when(currentUser.getId()).thenReturn(100L);

        final GmailCredentials cred1 = new GmailCredentials();
        cred1.setId(1L);
        cred1.setEmailAddress("email1@gmail.com");

        final GmailCredentials cred2 = new GmailCredentials();
        cred2.setId(2L);
        cred2.setEmailAddress("email2@gmail.com");

        when(gmailCredentialsRepository.findAllByUserId(100L))
                .thenReturn(Arrays.asList(cred1, cred2));

        final List<GmailAccountInfoDto> result = gmailCredentialsService.getMe();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("email1@gmail.com", result.get(0).getGmail());
        assertEquals("email2@gmail.com", result.get(1).getGmail());
        verify(currentUser, times(1)).getId();
        verify(gmailCredentialsRepository, times(1)).findAllByUserId(100L);
    }

    @Test
    void testGetMe_EmptyList() {
        when(currentUser.getId()).thenReturn(100L);
        when(gmailCredentialsRepository.findAllByUserId(100L))
                .thenReturn(Arrays.asList());

        final List<GmailAccountInfoDto> result = gmailCredentialsService.getMe();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(gmailCredentialsRepository, times(1)).findAllByUserId(100L);
    }

    @Test
    void testSendEmail_AccountNotFound() {
        when(gmailCredentialsRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            gmailCredentialsService.sendEmail(1L, "to@gmail.com", "Subject", "Body");
        });

        assertTrue(exception.getMessage().contains("Account not found"));
        verify(gmailCredentialsRepository, times(1)).findById(1L);
    }

    @Test
    void testSendEmailWithFile_AccountNotFound() {
        final MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".getBytes()
        );

        when(gmailCredentialsRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            gmailCredentialsService.sendEmailWithFile(1L, "to@gmail.com", "Subject", "Body", file);
        });

        assertTrue(exception.getMessage().contains("Account not found"));
        verify(gmailCredentialsRepository, times(1)).findById(1L);
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
        when(cryptoUtils.encrypt("plain_value")).thenReturn("encrypted_plain_value");

        final String encrypted = cryptoUtils.encrypt("plain_value");

        assertEquals("encrypted_plain_value", encrypted);
        verify(cryptoUtils, times(1)).encrypt("plain_value");
    }

    @Test
    void testRepositoryFindById() {
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
    void testCurrentUserGetId() {
        when(currentUser.getId()).thenReturn(100L);

        final Long userId = currentUser.getId();

        assertEquals(100L, userId);
        verify(currentUser, times(1)).getId();
    }

    @Test
    void testMultipleEncryptionCalls() throws Exception {
        when(cryptoUtils.encrypt("value1")).thenReturn("encrypted1");
        when(cryptoUtils.encrypt("value2")).thenReturn("encrypted2");

        final String encrypted1 = cryptoUtils.encrypt("value1");
        final String encrypted2 = cryptoUtils.encrypt("value2");

        assertEquals("encrypted1", encrypted1);
        assertEquals("encrypted2", encrypted2);
        verify(cryptoUtils, times(1)).encrypt("value1");
        verify(cryptoUtils, times(1)).encrypt("value2");
    }

    @Test
    void testCredentialsWithExpiredToken() {
        final GmailCredentials credentials = new GmailCredentials();
        credentials.setTokenExpiry(LocalDateTime.now().minusHours(1));

        assertTrue(credentials.getTokenExpiry().isBefore(LocalDateTime.now()));
    }

    @Test
    void testCredentialsWithValidToken() {
        final GmailCredentials credentials = new GmailCredentials();
        credentials.setTokenExpiry(LocalDateTime.now().plusHours(2));

        assertTrue(credentials.getTokenExpiry().isAfter(LocalDateTime.now()));
    }

    @Test
    void testRepositoryFindByUserId() {
        final GmailCredentials credentials = new GmailCredentials();
        credentials.setUserId(100L);
        credentials.setEmailAddress("test@gmail.com");

        when(gmailCredentialsRepository.findByUserId(100L)).thenReturn(Optional.of(credentials));

        final Optional<GmailCredentials> result = gmailCredentialsRepository.findByUserId(100L);

        assertTrue(result.isPresent());
        assertEquals("test@gmail.com", result.get().getEmailAddress());
        verify(gmailCredentialsRepository, times(1)).findByUserId(100L);
    }
}
