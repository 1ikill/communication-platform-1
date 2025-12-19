package com.sdc.telegram.controller;

import com.sdc.telegram.domain.dto.TelegramCredentialsCreateDto;
import com.sdc.telegram.service.TelegramCredentialService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.junit.jupiter.api.Assertions.assertThrows;import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TelegramCredentialsControllerTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private TelegramCredentialService service;
    
    @InjectMocks
    private TelegramCredentialsController telegramCredentialsController;
    
    private ObjectMapper objectMapper;
    
    private TelegramCredentialsCreateDto createDto;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(telegramCredentialsController).build();
        objectMapper = new ObjectMapper();
        
        createDto = new TelegramCredentialsCreateDto();
        createDto.setApiId("12345");
        createDto.setApiHash("abcdef123456");
        createDto.setAccountId("testAccount");
        createDto.setAccountName("Test Account");
        createDto.setPhoneNumber("+1234567890");
    }
    
    @Test
    void addCredentials_WithValidData_ShouldReturn200() throws Exception {
        doNothing().when(service).addCredentials(any(TelegramCredentialsCreateDto.class));
        
        mockMvc.perform(post("/telegram-credentials/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk());
        
        verify(service).addCredentials(any(TelegramCredentialsCreateDto.class));
    }
    
    @Test
    void addCredentials_WithMissingApiId_ShouldReturn400() throws Exception {
        createDto.setApiId(null);
        doNothing().when(service).addCredentials(any(TelegramCredentialsCreateDto.class));
        
        mockMvc.perform(post("/telegram-credentials/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void addCredentials_WithEmptyBody_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/telegram-credentials/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
        
        verify(service, never()).addCredentials(any());
    }
    
    @Test
    void addCredentials_WithInvalidJson_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/telegram-credentials/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());
        
        verify(service, never()).addCredentials(any());
    }
    
    @Test
    void addCredentials_WithServiceException_ShouldThrowException() throws Exception {
        doThrow(new RuntimeException("Failed to add credentials"))
            .when(service).addCredentials(any(TelegramCredentialsCreateDto.class));
        
        assertThrows(Exception.class, () ->
            mockMvc.perform(post("/telegram-credentials/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createDto))));
    }
    
    @Test
    void addCredentials_WithEncryptionException_ShouldThrowException() throws Exception {
        doThrow(new Exception("Encryption failed"))
            .when(service).addCredentials(any(TelegramCredentialsCreateDto.class));
        
        assertThrows(Exception.class, () ->
            mockMvc.perform(post("/telegram-credentials/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createDto))));
    }
    
    @Test
    void addCredentials_WithNullAccountId_ShouldReturn400() throws Exception {
        createDto.setAccountId(null);
        doNothing().when(service).addCredentials(any(TelegramCredentialsCreateDto.class));
        
        mockMvc.perform(post("/telegram-credentials/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void addCredentials_WithAllFields_ShouldCallService() throws Exception {
        doNothing().when(service).addCredentials(any(TelegramCredentialsCreateDto.class));
        
        mockMvc.perform(post("/telegram-credentials/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk());
        
        verify(service, times(1)).addCredentials(any(TelegramCredentialsCreateDto.class));
    }
}
