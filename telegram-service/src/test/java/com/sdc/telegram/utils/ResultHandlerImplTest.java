package com.sdc.telegram.utils;

import com.sdc.telegram.config.TelegramClientManager;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResultHandlerImplTest {
    
    @Mock
    private TelegramClientManager clientManager;
    
    @Mock
    private Client client;
    
    private ResultHandlerImpl resultHandler;
    
    private static final String ACCOUNT_ID = "testAccount123";
    
    @BeforeEach
    void setUp() {
        resultHandler = new ResultHandlerImpl();
        resultHandler.setClientManager(clientManager);
        resultHandler.setClient(client);
        
        when(clientManager.getAccountIdForClient(client)).thenReturn(ACCOUNT_ID);
    }
    
    @Test
    void onResult_WithAuthorizationStateWaitPhoneNumber_ShouldLogInfo() {
        TdApi.UpdateAuthorizationState update = new TdApi.UpdateAuthorizationState(
            new TdApi.AuthorizationStateWaitPhoneNumber()
        );
        
        resultHandler.onResult(update);
        
        verify(clientManager).getAccountIdForClient(client);
    }
    
    @Test
    void onResult_WithAuthorizationStateWaitCode_ShouldLogInfo() {
        TdApi.UpdateAuthorizationState update = new TdApi.UpdateAuthorizationState(
            new TdApi.AuthorizationStateWaitCode(
                new TdApi.AuthenticationCodeInfo(
                    "12345",
                    new TdApi.AuthenticationCodeTypeSms(1),
                    null,
                    60
                )
            )
        );
        
        resultHandler.onResult(update);
        
        verify(clientManager).getAccountIdForClient(client);
    }
    
    @Test
    void onResult_WithAuthorizationStateWaitPassword_ShouldLogInfo() {
        TdApi.UpdateAuthorizationState update = new TdApi.UpdateAuthorizationState(
            new TdApi.AuthorizationStateWaitPassword()
        );
        
        resultHandler.onResult(update);
        
        verify(clientManager).getAccountIdForClient(client);
    }
    
    @Test
    void onResult_WithAuthorizationStateReady_ShouldLogInfo() {
        TdApi.UpdateAuthorizationState update = new TdApi.UpdateAuthorizationState(
            new TdApi.AuthorizationStateReady()
        );
        
        resultHandler.onResult(update);
        
        verify(clientManager).getAccountIdForClient(client);
    }
    
    @Test
    void onResult_WithNullAccountId_ShouldHandleGracefully() {
        when(clientManager.getAccountIdForClient(client)).thenReturn(null);
        
        TdApi.UpdateAuthorizationState update = new TdApi.UpdateAuthorizationState(
            new TdApi.AuthorizationStateReady()
        );
        
        resultHandler.onResult(update);
        
        verify(clientManager).getAccountIdForClient(client);
    }
}
