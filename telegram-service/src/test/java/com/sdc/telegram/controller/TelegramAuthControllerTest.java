package com.sdc.telegram.controller;

import com.sdc.telegram.domain.dto.tdlib.auth.AuthorizationStateReadyDto;
import com.sdc.telegram.domain.dto.tdlib.auth.AuthorizationStateTdlib;
import com.sdc.telegram.service.TelegramAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TelegramAuthControllerTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private TelegramAuthService telegramAuthService;
    
    @InjectMocks
    private TelegramAuthController telegramAuthController;
    
    private static final String PHONE = "+1234567890";
    private static final String CODE = "123456";
    private static final String PASSWORD = "testPassword";
    private static final String ACCOUNT_ID = "testAccount";
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(telegramAuthController).build();
    }
    
    @Test
    void sendPhoneNumber_WithValidData_ShouldReturn200() throws Exception {
        doNothing().when(telegramAuthService).sendPhoneNumber(anyString(), anyString());
        
        mockMvc.perform(post("/telegram/auth/login/phone")
                .param("phone", PHONE)
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isOk());
        
        verify(telegramAuthService).sendPhoneNumber(PHONE, ACCOUNT_ID);
    }
    
    @Test
    void sendPhoneNumber_WithMissingPhone_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/telegram/auth/login/phone")
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isBadRequest());
        
        verify(telegramAuthService, never()).sendPhoneNumber(anyString(), anyString());
    }
    
    @Test
    void sendPhoneNumber_WithMissingAccountId_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/telegram/auth/login/phone")
                .param("phone", PHONE))
                .andExpect(status().isBadRequest());
        
        verify(telegramAuthService, never()).sendPhoneNumber(anyString(), anyString());
    }
    
    @Test
    void sendPhoneNumber_WithServiceException_ShouldThrowException() throws Exception {
        doThrow(new RuntimeException("Service error"))
            .when(telegramAuthService).sendPhoneNumber(anyString(), anyString());
        
        assertThrows(Exception.class, () ->
            mockMvc.perform(post("/telegram/auth/login/phone")
                    .param("phone", PHONE)
                    .param("accountId", ACCOUNT_ID)));
    }
    
    @Test
    void sendAuthCode_WithValidData_ShouldReturn200() throws Exception {
        doNothing().when(telegramAuthService).sendAuthCode(anyString(), anyString());
        
        mockMvc.perform(post("/telegram/auth/login/code")
                .param("code", CODE)
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isOk());
        
        verify(telegramAuthService).sendAuthCode(CODE, ACCOUNT_ID);
    }
    
    @Test
    void sendAuthCode_WithMissingCode_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/telegram/auth/login/code")
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isBadRequest());
        
        verify(telegramAuthService, never()).sendAuthCode(anyString(), anyString());
    }
    
    @Test
    void sendAuthCode_WithServiceException_ShouldThrowException() throws Exception {
        doThrow(new RuntimeException("Invalid code"))
            .when(telegramAuthService).sendAuthCode(anyString(), anyString());
        
        assertThrows(Exception.class, () ->
            mockMvc.perform(post("/telegram/auth/login/code")
                    .param("code", CODE)
                    .param("accountId", ACCOUNT_ID)));
    }
    
    @Test
    void sendPassword_WithValidData_ShouldReturn200() throws Exception {
        doNothing().when(telegramAuthService).sendPassword(anyString(), anyString());
        
        mockMvc.perform(post("/telegram/auth/login/password")
                .param("password", PASSWORD)
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isOk());
        
        verify(telegramAuthService).sendPassword(PASSWORD, ACCOUNT_ID);
    }
    
    @Test
    void sendPassword_WithMissingPassword_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/telegram/auth/login/password")
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isBadRequest());
        
        verify(telegramAuthService, never()).sendPassword(anyString(), anyString());
    }
    
    @Test
    void logout_WithValidAccountId_ShouldReturn200() throws Exception {
        doNothing().when(telegramAuthService).logout(anyString());
        
        mockMvc.perform(post("/telegram/auth/logout")
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isOk());
        
        verify(telegramAuthService).logout(ACCOUNT_ID);
    }
    
    @Test
    void logout_WithMissingAccountId_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/telegram/auth/logout"))
                .andExpect(status().isBadRequest());
        
        verify(telegramAuthService, never()).logout(anyString());
    }
    
    @Test
    void logout_WithServiceException_ShouldThrowException() throws Exception {
        doThrow(new IllegalArgumentException("Invalid accountId"))
            .when(telegramAuthService).logout(anyString());
        
        assertThrows(Exception.class, () ->
            mockMvc.perform(post("/telegram/auth/logout")
                    .param("accountId", ACCOUNT_ID)));
    }
    
    @Test
    void getAuthorizationState_WithValidAccountId_ShouldReturnState() throws Exception {
        AuthorizationStateReadyDto state = new AuthorizationStateReadyDto();
        when(telegramAuthService.getAuthorizationState(anyString())).thenReturn(state);
        
        mockMvc.perform(get("/telegram/auth/state")
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isOk());
        
        verify(telegramAuthService).getAuthorizationState(ACCOUNT_ID);
    }
    
    @Test
    void getAuthorizationState_WithMissingAccountId_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/telegram/auth/state"))
                .andExpect(status().isBadRequest());
        
        verify(telegramAuthService, never()).getAuthorizationState(anyString());
    }
    
    @Test
    void getAuthorizationState_WithServiceException_ShouldThrowException() throws Exception {
        when(telegramAuthService.getAuthorizationState(anyString()))
            .thenThrow(new RuntimeException("Failed to get state"));
        
        assertThrows(Exception.class, () ->
            mockMvc.perform(get("/telegram/auth/state")
                    .param("accountId", ACCOUNT_ID)));
    }
}
