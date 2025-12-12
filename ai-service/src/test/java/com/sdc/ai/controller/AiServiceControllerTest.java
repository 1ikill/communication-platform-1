package com.sdc.ai.controller;

import com.sdc.ai.domain.constants.CommunicationPlatformType;
import com.sdc.ai.domain.dto.ContactProfileCreateDto;
import com.sdc.ai.domain.dto.ContactProfileDto;
import com.sdc.ai.domain.dto.ContactProfilePatchDto;
import com.sdc.ai.service.AIMessageFormattingService;
import com.sdc.ai.service.ContactProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiServiceControllerTest {

    @Mock
    private AIMessageFormattingService messageFormattingService;

    @Mock
    private ContactProfileService profileService;

    @InjectMocks
    private AiServiceController aiServiceController;

    private ContactProfileCreateDto createDto;
    private ContactProfileDto profileDto;
    private ContactProfilePatchDto patchDto;

    @BeforeEach
    void setUp() {
        createDto = new ContactProfileCreateDto();
        
        profileDto = new ContactProfileDto();
        profileDto.setId(1L);
        profileDto.setContactName("John Doe");
        
        patchDto = new ContactProfilePatchDto();
    }

    @Test
    void addProfile_ShouldReturnCreatedProfile() {
        // Arrange
        when(profileService.createContactProfile(any(ContactProfileCreateDto.class)))
            .thenReturn(profileDto);

        // Act
        ContactProfileDto result = aiServiceController.addProfile(createDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getContactName());
        verify(profileService, times(1)).createContactProfile(createDto);
    }

    @Test
    void addProfile_ShouldCallServiceWithCorrectDto() {
        // Arrange
        when(profileService.createContactProfile(any(ContactProfileCreateDto.class)))
            .thenReturn(profileDto);

        // Act
        aiServiceController.addProfile(createDto);

        // Assert
        verify(profileService).createContactProfile(createDto);
    }

    @Test
    void patchProfile_ShouldReturnUpdatedProfile() {
        // Arrange
        Long profileId = 1L;
        ContactProfileDto updatedDto = new ContactProfileDto();
        updatedDto.setId(profileId);
        updatedDto.setContactName("Jane Doe");
        
        when(profileService.patch(eq(profileId), any(ContactProfilePatchDto.class)))
            .thenReturn(updatedDto);

        // Act
        ContactProfileDto result = aiServiceController.patchProfile(profileId, patchDto);

        // Assert
        assertNotNull(result);
        assertEquals(profileId, result.getId());
        assertEquals("Jane Doe", result.getContactName());
        verify(profileService, times(1)).patch(profileId, patchDto);
    }

    @Test
    void patchProfile_ShouldCallServiceWithCorrectParameters() {
        // Arrange
        Long profileId = 5L;
        when(profileService.patch(anyLong(), any(ContactProfilePatchDto.class)))
            .thenReturn(profileDto);

        // Act
        aiServiceController.patchProfile(profileId, patchDto);

        // Assert
        verify(profileService).patch(profileId, patchDto);
    }

    @Test
    void formatMessage_ShouldReturnFormattedMessage() {
        // Arrange
        CommunicationPlatformType platform = CommunicationPlatformType.TELEGRAM;
        String chatIdentifier = "chat123";
        String message = "Hello World";
        String formattedMessage = "Hello World, personalized!";
        
        when(messageFormattingService.formatMessage(anyString(), any(CommunicationPlatformType.class), anyString()))
            .thenReturn(formattedMessage);

        // Act
        String result = aiServiceController.formatMessage(platform, chatIdentifier, message);

        // Assert
        assertNotNull(result);
        assertEquals(formattedMessage, result);
        verify(messageFormattingService, times(1)).formatMessage(message, platform, chatIdentifier);
    }

    @Test
    void formatMessage_ShouldCallServiceWithCorrectParameters() {
        // Arrange
        CommunicationPlatformType platform = CommunicationPlatformType.EMAIL;
        String chatIdentifier = "email@test.com";
        String message = "Test message";
        
        when(messageFormattingService.formatMessage(anyString(), any(CommunicationPlatformType.class), anyString()))
            .thenReturn(message);

        // Act
        aiServiceController.formatMessage(platform, chatIdentifier, message);

        // Assert
        verify(messageFormattingService).formatMessage(message, platform, chatIdentifier);
    }

    @Test
    void formatMessage_WithWhatsApp_ShouldReturnFormattedMessage() {
        // Arrange
        CommunicationPlatformType platform = CommunicationPlatformType.DISCORD;
        String chatIdentifier = "+1234567890";
        String message = "Meeting reminder";
        String expected = "Hey! Meeting reminder";
        
        when(messageFormattingService.formatMessage(message, platform, chatIdentifier))
            .thenReturn(expected);

        // Act
        String result = aiServiceController.formatMessage(platform, chatIdentifier, message);

        // Assert
        assertEquals(expected, result);
        verify(messageFormattingService).formatMessage(message, platform, chatIdentifier);
    }

    @Test
    void formatMessage_WithViber_ShouldReturnFormattedMessage() {
        // Arrange
        CommunicationPlatformType platform = CommunicationPlatformType.VIBER;
        String chatIdentifier = "viber_user_123";
        String message = "Quick update";
        String expected = "Hi there! Quick update";
        
        when(messageFormattingService.formatMessage(message, platform, chatIdentifier))
            .thenReturn(expected);

        // Act
        String result = aiServiceController.formatMessage(platform, chatIdentifier, message);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void addProfile_WithNullDto_ShouldStillCallService() {
        // Arrange
        when(profileService.createContactProfile(null))
            .thenReturn(profileDto);

        // Act
        ContactProfileDto result = aiServiceController.addProfile(null);

        // Assert
        assertNotNull(result);
        verify(profileService).createContactProfile(null);
    }
}
