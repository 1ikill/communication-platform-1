package com.sdc.telegram.service;

import com.sdc.telegram.config.TelegramClientManager;
import com.sdc.telegram.domain.dto.tdlib.auth.AuthorizationStateTdlib;
import com.sdc.telegram.domain.mapper.auth.AuthorizationStateTdlibMapper;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramAuthServiceTest {
    
    @Mock
    private TelegramClientManager clientManager;
    
    @Mock
    private AuthorizationStateTdlibMapper authorizationStateTdlibMapper;
    
    @Mock
    private Client client;
    
    @Captor
    private ArgumentCaptor<TdApi.Function> functionCaptor;
    
    @Captor
    private ArgumentCaptor<Client.ResultHandler> handlerCaptor;
    
    private TelegramAuthService telegramAuthService;
    
    private static final String ACCOUNT_ID = "testAccount123";
    private static final String PHONE = "+1234567890";
    private static final String CODE = "123456";
    private static final String PASSWORD = "testPassword";
    
    @BeforeEach
    void setUp() {
        telegramAuthService = new TelegramAuthService(clientManager, authorizationStateTdlibMapper);
        lenient().when(clientManager.getClient(ACCOUNT_ID)).thenReturn(client);
    }
    
    @Test
    void sendPhoneNumber_WithValidAccountId_ShouldSendPhoneNumber() {
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(new TdApi.AuthorizationStateWaitPhoneNumber());
            return null;
        }).when(client).send(any(TdApi.GetAuthorizationState.class), any());
        
        doAnswer(invocation -> null)
            .when(client).send(any(TdApi.SetAuthenticationPhoneNumber.class), isNull());
        
        telegramAuthService.sendPhoneNumber(PHONE, ACCOUNT_ID);
        
        verify(clientManager).getClient(ACCOUNT_ID);
        verify(client, times(2)).send(any(), any());
    }
    
    @Test
    void sendPhoneNumber_WithInvalidAccountId_ShouldThrowException() {
        when(clientManager.getClient("invalidId")).thenReturn(null);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> telegramAuthService.sendPhoneNumber(PHONE, "invalidId")
        );
        
        assertTrue(exception.getMessage().contains("Invalid accountId"));
    }
    
    @Test
    void sendPhoneNumber_WithErrorResponse_ShouldThrowException() {
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(new TdApi.Error(400, "Bad Request"));
            return null;
        }).when(client).send(any(TdApi.GetAuthorizationState.class), any());
        
        assertThrows(UnsatisfiedLinkError.class,
            () -> telegramAuthService.sendPhoneNumber(PHONE, ACCOUNT_ID));
    }
    
    @Test
    void sendAuthCode_WithValidCode_ShouldSendCode() {
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(new TdApi.AuthorizationStateWaitCode(null));
            return null;
        }).when(client).send(any(TdApi.GetAuthorizationState.class), any());
        
        doAnswer(invocation -> null)
            .when(client).send(any(TdApi.CheckAuthenticationCode.class), isNull());
        
        telegramAuthService.sendAuthCode(CODE, ACCOUNT_ID);
        
        verify(client, times(2)).send(any(), any());
    }
    
    @Test
    void sendAuthCode_WithErrorResponse_ShouldThrowException() {
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(new TdApi.Error(401, "Invalid code"));
            return null;
        }).when(client).send(any(TdApi.GetAuthorizationState.class), any());
        
        assertThrows(UnsatisfiedLinkError.class,
            () -> telegramAuthService.sendAuthCode(CODE, ACCOUNT_ID));
    }
    
    @Test
    void sendPassword_WithValidPassword_ShouldSendPassword() {
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(new TdApi.AuthorizationStateWaitPassword());
            return null;
        }).when(client).send(any(TdApi.GetAuthorizationState.class), any());
        
        doAnswer(invocation -> null)
            .when(client).send(any(TdApi.CheckAuthenticationPassword.class), isNull());
        
        telegramAuthService.sendPassword(PASSWORD, ACCOUNT_ID);
        
        verify(client, times(2)).send(any(), any());
    }
    
    @Test
    void sendPassword_WithErrorResponse_ShouldThrowException() {
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(new TdApi.Error(401, "Invalid password"));
            return null;
        }).when(client).send(any(TdApi.GetAuthorizationState.class), any());
        
        assertThrows(UnsatisfiedLinkError.class,
            () -> telegramAuthService.sendPassword(PASSWORD, ACCOUNT_ID));
    }
    
    @Test
    void logout_WithValidAccountId_ShouldLogout() {
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(new TdApi.Ok());
            return null;
        }).when(client).send(any(TdApi.LogOut.class), any());
        
        telegramAuthService.logout(ACCOUNT_ID);
        
        verify(clientManager).getClient(ACCOUNT_ID);
        verify(client).send(any(TdApi.LogOut.class), any());
    }
    
    @Test
    void logout_WithInvalidAccountId_ShouldThrowException() {
        when(clientManager.getClient("invalidId")).thenReturn(null);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> telegramAuthService.logout("invalidId")
        );
        
        assertTrue(exception.getMessage().contains("Invalid accountId"));
    }
    
    @Test
    void logout_WithErrorResponse_ShouldLogError() {
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(new TdApi.Error(500, "Server error"));
            return null;
        }).when(client).send(any(TdApi.LogOut.class), any());
        
        telegramAuthService.logout(ACCOUNT_ID);
        
        verify(client).send(any(TdApi.LogOut.class), any());
    }
    
    @Test
    void getAuthorizationState_WithReadyState_ShouldReturnState() throws ExecutionException, InterruptedException {
        TdApi.AuthorizationStateReady readyState = new TdApi.AuthorizationStateReady();
        AuthorizationStateTdlib expectedDto = mock(AuthorizationStateTdlib.class);
        
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(readyState);
            return null;
        }).when(client).send(any(TdApi.GetAuthorizationState.class), any());
        
        when(authorizationStateTdlibMapper.toDto(readyState)).thenReturn(expectedDto);
        
        AuthorizationStateTdlib result = telegramAuthService.getAuthorizationState(ACCOUNT_ID);
        
        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(authorizationStateTdlibMapper).toDto(readyState);
    }
    
    @Test
    void getAuthorizationState_WithWaitPhoneState_ShouldReturnState() throws ExecutionException, InterruptedException {
        TdApi.AuthorizationStateWaitPhoneNumber waitPhoneState = new TdApi.AuthorizationStateWaitPhoneNumber();
        AuthorizationStateTdlib expectedDto = mock(AuthorizationStateTdlib.class);
        
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(waitPhoneState);
            return null;
        }).when(client).send(any(TdApi.GetAuthorizationState.class), any());
        
        when(authorizationStateTdlibMapper.toDto(waitPhoneState)).thenReturn(expectedDto);
        
        AuthorizationStateTdlib result = telegramAuthService.getAuthorizationState(ACCOUNT_ID);
        
        assertNotNull(result);
        assertEquals(expectedDto, result);
    }
    
    @Test
    void getAuthorizationState_WithErrorResponse_ShouldThrowException() {
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(new TdApi.Error(500, "Internal error"));
            return null;
        }).when(client).send(any(TdApi.GetAuthorizationState.class), any());
        
        assertThrows(UnsatisfiedLinkError.class,
            () -> telegramAuthService.getAuthorizationState(ACCOUNT_ID));
    }
}
