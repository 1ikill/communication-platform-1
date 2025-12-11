package com.sdc.telegram.service;

import com.sdc.telegram.config.TelegramClientManager;
import com.sdc.telegram.config.security.CurrentUser;
import com.sdc.telegram.domain.dto.TelegramCredentialsCreateDto;
import com.sdc.telegram.domain.mapper.TelegramCredentialsMapper;
import com.sdc.telegram.domain.model.TelegramCredentials;
import com.sdc.telegram.repository.TelegramCredentialsRepository;
import com.sdc.telegram.utils.CryptoUtils;
import com.sdc.telegram.utils.ResultHandlerImpl;
import org.drinkless.tdlib.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramCredentialServiceTest {
    
    @Mock
    private TelegramCredentialsRepository repository;
    
    @Mock
    private TelegramCredentialsMapper mapper;
    
    @Mock
    private CryptoUtils cryptoUtils;
    
    @Mock
    private CurrentUser currentUser;
    
    @Mock
    private ResultHandlerImpl resultHandler;
    
    @Mock
    private TelegramClientManager clientManager;
    
    @Mock
    private Client client;
    
    private TelegramCredentialService telegramCredentialService;
    
    private TelegramCredentialsCreateDto createDto;
    private TelegramCredentials credentials;
    
    private static final Long USER_ID = 123L;
    private static final String API_ID = "12345";
    private static final String API_HASH = "abcdef123456";
    private static final String ACCOUNT_ID = "testAccount";
    private static final String ACCOUNT_NAME = "Test Account";
    private static final String PHONE_NUMBER = "+1234567890";
    private static final String ENCRYPTED_API_ID = "encryptedApiId";
    private static final String ENCRYPTED_API_HASH = "encryptedApiHash";
    
    @BeforeEach
    void setUp() {
        telegramCredentialService = new TelegramCredentialService(
            repository, mapper, cryptoUtils, currentUser, resultHandler, clientManager
        );
        
        createDto = new TelegramCredentialsCreateDto();
        createDto.setApiId(API_ID);
        createDto.setApiHash(API_HASH);
        createDto.setAccountId(ACCOUNT_ID);
        createDto.setAccountName(ACCOUNT_NAME);
        createDto.setPhoneNumber(PHONE_NUMBER);
        
        credentials = new TelegramCredentials();
        credentials.setAccountId(ACCOUNT_ID);
        credentials.setAccountName(ACCOUNT_NAME);
        credentials.setPhoneNumber(PHONE_NUMBER);
    }
    
    @Test
    void addCredentials_WithValidData_ShouldSaveCredentials() throws Exception {
        when(mapper.fromCreateDto(createDto)).thenReturn(credentials);
        when(currentUser.getId()).thenReturn(USER_ID);
        when(cryptoUtils.encrypt(API_ID)).thenReturn(ENCRYPTED_API_ID);
        when(cryptoUtils.encrypt(API_HASH)).thenReturn(ENCRYPTED_API_HASH);
        doNothing().when(clientManager).initializeClient(any(Client.class), any(TelegramCredentials.class));
        doNothing().when(clientManager).putClient(any(Client.class), anyString());
        doNothing().when(clientManager).putAccountId(anyString(), any(Client.class));
        when(repository.save(any(TelegramCredentials.class))).thenReturn(credentials);
        
        try (MockedStatic<Client> clientMock = mockStatic(Client.class)) {
            clientMock.when(() -> Client.create(any(), any(), any())).thenReturn(client);
            
            telegramCredentialService.addCredentials(createDto);
            
            verify(mapper).fromCreateDto(createDto);
            verify(currentUser).getId();
            verify(cryptoUtils).encrypt(API_ID);
            verify(cryptoUtils).encrypt(API_HASH);
            verify(clientManager).putClient(any(Client.class), eq(ACCOUNT_ID));
            verify(clientManager).putAccountId(eq(ACCOUNT_ID), any(Client.class));
            verify(repository).save(credentials);
        }
    }
    
    @Test
    void addCredentials_WithEncryptionFailure_ShouldThrowException() throws Exception {
        when(mapper.fromCreateDto(createDto)).thenReturn(credentials);
        when(currentUser.getId()).thenReturn(USER_ID);
        when(cryptoUtils.encrypt(API_ID)).thenThrow(new RuntimeException("Encryption failed"));
        
        assertThrows(Exception.class, () -> telegramCredentialService.addCredentials(createDto));
        
        verify(repository, never()).save(any());
    }
    
    @Test
    void addCredentials_WithNullApiId_ShouldThrowException() throws Exception {
        createDto.setApiId(null);
        when(mapper.fromCreateDto(createDto)).thenReturn(credentials);
        when(currentUser.getId()).thenReturn(USER_ID);
        when(cryptoUtils.encrypt(null)).thenThrow(new NullPointerException("Cannot encrypt null"));
        
        assertThrows(Exception.class, () -> telegramCredentialService.addCredentials(createDto));
    }
    
    @Test
    void addCredentials_WithNullApiHash_ShouldThrowException() throws Exception {
        createDto.setApiHash(null);
        when(mapper.fromCreateDto(createDto)).thenReturn(credentials);
        when(currentUser.getId()).thenReturn(USER_ID);
        when(cryptoUtils.encrypt(API_ID)).thenReturn(ENCRYPTED_API_ID);
        when(cryptoUtils.encrypt(null)).thenThrow(new NullPointerException("Cannot encrypt null"));
        
        assertThrows(Exception.class, () -> telegramCredentialService.addCredentials(createDto));
    }
    
    @Test
    void addCredentials_ShouldSetUserId() throws Exception {
        when(mapper.fromCreateDto(createDto)).thenReturn(credentials);
        when(currentUser.getId()).thenReturn(USER_ID);
        when(cryptoUtils.encrypt(anyString())).thenReturn("encrypted");
        doNothing().when(clientManager).initializeClient(any(), any());
        doNothing().when(clientManager).putClient(any(), anyString());
        doNothing().when(clientManager).putAccountId(anyString(), any());
        when(repository.save(any())).thenReturn(credentials);
        
        try (MockedStatic<Client> clientMock = mockStatic(Client.class)) {
            clientMock.when(() -> Client.create(any(), any(), any())).thenReturn(client);
            
            telegramCredentialService.addCredentials(createDto);
            
            verify(currentUser).getId();
        }
    }
    
    @Test
    void addCredentials_ShouldEncryptBothApiIdAndHash() throws Exception {
        when(mapper.fromCreateDto(createDto)).thenReturn(credentials);
        when(currentUser.getId()).thenReturn(USER_ID);
        when(cryptoUtils.encrypt(API_ID)).thenReturn(ENCRYPTED_API_ID);
        when(cryptoUtils.encrypt(API_HASH)).thenReturn(ENCRYPTED_API_HASH);
        doNothing().when(clientManager).initializeClient(any(), any());
        doNothing().when(clientManager).putClient(any(), anyString());
        doNothing().when(clientManager).putAccountId(anyString(), any());
        when(repository.save(any())).thenReturn(credentials);
        
        try (MockedStatic<Client> clientMock = mockStatic(Client.class)) {
            clientMock.when(() -> Client.create(any(), any(), any())).thenReturn(client);
            
            telegramCredentialService.addCredentials(createDto);
            
            verify(cryptoUtils).encrypt(API_ID);
            verify(cryptoUtils).encrypt(API_HASH);
        }
    }
    
    @Test
    void addCredentials_ShouldInitializeClientBeforeSaving() throws Exception {
        when(mapper.fromCreateDto(createDto)).thenReturn(credentials);
        when(currentUser.getId()).thenReturn(USER_ID);
        when(cryptoUtils.encrypt(anyString())).thenReturn("encrypted");
        doNothing().when(clientManager).initializeClient(any(), any());
        doNothing().when(clientManager).putClient(any(), anyString());
        doNothing().when(clientManager).putAccountId(anyString(), any());
        when(repository.save(any())).thenReturn(credentials);
        
        try (MockedStatic<Client> clientMock = mockStatic(Client.class)) {
            clientMock.when(() -> Client.create(any(), any(), any())).thenReturn(client);
            
            telegramCredentialService.addCredentials(createDto);
            
            verify(clientManager).initializeClient(any(Client.class), eq(credentials));
            verify(repository).save(credentials);
        }
    }
}
