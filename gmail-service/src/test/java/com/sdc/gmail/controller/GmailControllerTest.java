package com.sdc.gmail.controller;

import com.sdc.gmail.domain.dto.GmailAccountInfoDto;
import com.sdc.gmail.service.GmailCredentialsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GmailControllerTest {

    @Mock
    private GmailCredentialsService gmailCredentialsService;

    @InjectMocks
    private GmailController gmailController;

    @Mock
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        // Setup common mocks if needed
    }

    @Test
    void testGetAuthUrl() throws Exception {
        final String expectedUrl = "https://accounts.google.com/o/oauth2/auth?client_id=123";
        
        when(gmailCredentialsService.buildAuthorizationUrl(any(MultipartFile.class)))
                .thenReturn(expectedUrl);
        
        final String result = gmailController.getAuthUrl(multipartFile);
        
        assertEquals(expectedUrl, result);
        verify(gmailCredentialsService, times(1)).buildAuthorizationUrl(multipartFile);
    }

    @Test
    void testGetAuthUrlThrowsException() throws Exception {
        when(gmailCredentialsService.buildAuthorizationUrl(any(MultipartFile.class)))
                .thenThrow(new RuntimeException("Invalid file"));
        
        assertThrows(Exception.class, () -> gmailController.getAuthUrl(multipartFile));
    }

    @Test
    void testCallback() throws Exception {
        final String code = "auth_code";
        final String state = "userId=1&accountId=2";
        final String expectedEmail = "test@gmail.com";
        
        when(gmailCredentialsService.handleOAuthCallback(anyString(), anyString()))
                .thenReturn(expectedEmail);
        
        final String result = gmailController.callback(code, state);
        
        assertEquals(expectedEmail, result);
        verify(gmailCredentialsService, times(1)).handleOAuthCallback(code, state);
    }

    @Test
    void testCallbackWithNullState() throws Exception {
        final String code = "auth_code";
        final String expectedEmail = "test@gmail.com";
        
        when(gmailCredentialsService.handleOAuthCallback(anyString(), isNull()))
                .thenReturn(expectedEmail);
        
        final String result = gmailController.callback(code, null);
        
        assertEquals(expectedEmail, result);
        verify(gmailCredentialsService, times(1)).handleOAuthCallback(code, null);
    }

    @Test
    void testSendEmail() throws Exception {
        final Long accountId = 1L;
        final String to = "recipient@gmail.com";
        final String subject = "Test Subject";
        final String body = "Test Body";
        
        doNothing().when(gmailCredentialsService).sendEmail(anyLong(), anyString(), anyString(), anyString());
        
        gmailController.sendEmail(accountId, to, subject, body);
        
        verify(gmailCredentialsService, times(1)).sendEmail(accountId, to, subject, body);
    }

    @Test
    void testSendEmailThrowsException() throws Exception {
        final Long accountId = 1L;
        final String to = "recipient@gmail.com";
        final String subject = "Test Subject";
        final String body = "Test Body";
        
        doThrow(new RuntimeException("Send failed"))
                .when(gmailCredentialsService).sendEmail(anyLong(), anyString(), anyString(), anyString());
        
        assertThrows(Exception.class, () -> gmailController.sendEmail(accountId, to, subject, body));
    }

    @Test
    void testSendEmailWithFile() throws Exception {
        final Long accountId = 1L;
        final String to = "recipient@gmail.com";
        final String subject = "Test Subject";
        final String body = "Test Body";
        
        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
        doNothing().when(gmailCredentialsService).sendEmailWithFile(anyLong(), anyString(), anyString(), anyString(), any(MultipartFile.class));
        
        gmailController.sendEmailWithFile(accountId, to, subject, body, multipartFile);
        
        verify(gmailCredentialsService, times(1)).sendEmailWithFile(accountId, to, subject, body, multipartFile);
    }

    @Test
    void testSendEmailWithFileThrowsException() throws Exception {
        final Long accountId = 1L;
        final String to = "recipient@gmail.com";
        final String subject = "Test Subject";
        final String body = "Test Body";
        
        doThrow(new RuntimeException("Send failed"))
                .when(gmailCredentialsService).sendEmailWithFile(anyLong(), anyString(), anyString(), anyString(), any(MultipartFile.class));
        
        assertThrows(Exception.class, () -> gmailController.sendEmailWithFile(accountId, to, subject, body, multipartFile));
    }

    @Test
    void testGetMe() {
        final GmailAccountInfoDto account1 = new GmailAccountInfoDto();
        account1.setAccountId(1L);
        account1.setGmail("user1@gmail.com");
        
        final GmailAccountInfoDto account2 = new GmailAccountInfoDto();
        account2.setAccountId(2L);
        account2.setGmail("user2@gmail.com");
        
        when(gmailCredentialsService.getMe()).thenReturn(List.of(account1, account2));
        
        final List<GmailAccountInfoDto> result = gmailController.getMe();
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1@gmail.com", result.get(0).getGmail());
        assertEquals("user2@gmail.com", result.get(1).getGmail());
        verify(gmailCredentialsService, times(1)).getMe();
    }

    @Test
    void testGetMeReturnsEmptyList() {
        when(gmailCredentialsService.getMe()).thenReturn(List.of());
        
        final List<GmailAccountInfoDto> result = gmailController.getMe();
        
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(gmailCredentialsService, times(1)).getMe();
    }
}
