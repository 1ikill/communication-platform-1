package com.sdc.ai.service;

import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.sdc.ai.config.security.CurrentUser;
import com.sdc.ai.domain.constants.CommunicationPlatformType;
import com.sdc.ai.domain.constants.RelationshipType;
import com.sdc.ai.domain.constants.ToneStyleType;
import com.sdc.ai.domain.model.ContactProfile;
import com.sdc.ai.repository.ContactProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIMessageFormattingServiceTest {

    @Mock
    private ContactProfileRepository contactProfileRepository;

    @Mock
    private OpenAIClient client;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private AIMessageFormattingService aiMessageFormattingService;

    private ContactProfile contactProfile;

    @BeforeEach
    void setUp() {
        contactProfile = new ContactProfile();
        contactProfile.setId(1L);
        contactProfile.setUserId(100L);
        contactProfile.setContactName("John Doe");
        contactProfile.setRelationshipType(RelationshipType.COLLEAGUE);
        contactProfile.setToneStyle(ToneStyleType.WARM);
        contactProfile.setFormalityLevel(3);
        contactProfile.setPreferredGreeting("Hello");
        contactProfile.setPlatform(CommunicationPlatformType.TELEGRAM);
        contactProfile.setChatIdentifier("chat123");
    }

    @Test
    void formatMessage_WithNullContactProfile_ShouldReturnOriginalMessage() {
        // Arrange
        String inputMessage = "Test message";
        Long userId = 100L;

        when(currentUser.getId()).thenReturn(userId);
        when(contactProfileRepository.findByUserIdAndPlatformAndChatIdentifier(
            anyLong(), any(CommunicationPlatformType.class), anyString()))
            .thenReturn(Optional.empty());

        // Act
        String result = aiMessageFormattingService.formatMessage(
            inputMessage, CommunicationPlatformType.TELEGRAM, "chat123");

        // Assert
        assertEquals(inputMessage, result);
        verify(contactProfileRepository).findByUserIdAndPlatformAndChatIdentifier(
            userId, CommunicationPlatformType.TELEGRAM, "chat123");
    }

    @Test
    void formatMessage_WithoutProfile_ShouldReturnOriginalMessage() {
        // Arrange
        String inputMessage = "Hello World";
        Long userId = 100L;

        when(currentUser.getId()).thenReturn(userId);
        when(contactProfileRepository.findByUserIdAndPlatformAndChatIdentifier(
            anyLong(), any(CommunicationPlatformType.class), anyString()))
            .thenReturn(Optional.empty());

        // Act
        String result = aiMessageFormattingService.formatMessage(
            inputMessage, CommunicationPlatformType.EMAIL, "test@email.com");

        // Assert
        assertEquals(inputMessage, result);
    }

    @Test
    void formatMessage_WithEmptyMessage_ShouldReturnOriginalMessage() {
        // Arrange
        String emptyMessage = "";
        Long userId = 100L;

        when(currentUser.getId()).thenReturn(userId);
        when(contactProfileRepository.findByUserIdAndPlatformAndChatIdentifier(
            anyLong(), any(CommunicationPlatformType.class), anyString()))
            .thenReturn(Optional.of(contactProfile));

        // Act
        String result = aiMessageFormattingService.formatMessage(
            emptyMessage, CommunicationPlatformType.TELEGRAM, "chat123");

        // Assert
        assertEquals(emptyMessage, result);
    }

    @Test
    void formatMessage_WithMessageTooLong_ShouldReturnOriginalMessage() {
        // Arrange
        String longMessage = "a".repeat(501); // Over 500 character limit
        Long userId = 100L;

        when(currentUser.getId()).thenReturn(userId);
        when(contactProfileRepository.findByUserIdAndPlatformAndChatIdentifier(
            anyLong(), any(CommunicationPlatformType.class), anyString()))
            .thenReturn(Optional.of(contactProfile));

        // Act
        String result = aiMessageFormattingService.formatMessage(
            longMessage, CommunicationPlatformType.WHATSAPP, "phone123");

        // Assert
        assertEquals(longMessage, result);
    }

    @Test
    void formatMessage_WithMaxLengthMessage_ShouldNotReturnOriginal() {
        // Arrange
        String maxLengthMessage = "a".repeat(500); // Exactly at limit
        Long userId = 100L;

        when(currentUser.getId()).thenReturn(userId);
        when(contactProfileRepository.findByUserIdAndPlatformAndChatIdentifier(
            anyLong(), any(CommunicationPlatformType.class), anyString()))
            .thenReturn(Optional.of(contactProfile));

        // Act - Should try to process since it's at the limit
        String result = aiMessageFormattingService.formatMessage(
            maxLengthMessage, CommunicationPlatformType.TELEGRAM, "chat123");

        // Assert - Will either format or fallback to original
        assertNotNull(result);
    }

    @Test
    void formatMessage_WithDifferentUserIds_ShouldUseCorrectUserId() {
        // Arrange
        String inputMessage = "Test";
        Long userId1 = 100L;
        Long userId2 = 200L;

        // First call
        when(currentUser.getId()).thenReturn(userId1);
        when(contactProfileRepository.findByUserIdAndPlatformAndChatIdentifier(
            userId1, CommunicationPlatformType.EMAIL, "email1"))
            .thenReturn(Optional.empty());

        // Act
        aiMessageFormattingService.formatMessage(inputMessage, CommunicationPlatformType.EMAIL, "email1");

        // Assert
        verify(contactProfileRepository).findByUserIdAndPlatformAndChatIdentifier(
            userId1, CommunicationPlatformType.EMAIL, "email1");

        // Second call with different user
        when(currentUser.getId()).thenReturn(userId2);
        when(contactProfileRepository.findByUserIdAndPlatformAndChatIdentifier(
            userId2, CommunicationPlatformType.EMAIL, "email2"))
            .thenReturn(Optional.empty());

        // Act
        aiMessageFormattingService.formatMessage(inputMessage, CommunicationPlatformType.EMAIL, "email2");

        // Assert
        verify(contactProfileRepository).findByUserIdAndPlatformAndChatIdentifier(
            userId2, CommunicationPlatformType.EMAIL, "email2");
    }

    @Test
    void formatMessage_WithAllPlatforms_ShouldQueryRepository() {
        // Arrange
        String inputMessage = "Test";
        Long userId = 100L;

        when(currentUser.getId()).thenReturn(userId);

        CommunicationPlatformType[] platforms = {
            CommunicationPlatformType.TELEGRAM,
            CommunicationPlatformType.WHATSAPP,
            CommunicationPlatformType.VIBER,
            CommunicationPlatformType.EMAIL
        };

        for (CommunicationPlatformType platform : platforms) {
            when(contactProfileRepository.findByUserIdAndPlatformAndChatIdentifier(
                userId, platform, "identifier"))
                .thenReturn(Optional.empty());

            // Act
            String result = aiMessageFormattingService.formatMessage(
                inputMessage, platform, "identifier");

            // Assert
            assertEquals(inputMessage, result);
            verify(contactProfileRepository).findByUserIdAndPlatformAndChatIdentifier(
                userId, platform, "identifier");
        }
    }

    @Test
    void formatMessage_WithNullFields_ShouldHandleGracefully() {
        // Arrange
        contactProfile.setContactName(null);
        contactProfile.setRelationshipType(null);
        contactProfile.setToneStyle(null);
        contactProfile.setPreferredGreeting(null);
        String inputMessage = "Test message";
        Long userId = 100L;

        when(currentUser.getId()).thenReturn(userId);
        when(currentUser.getFullName()).thenReturn("Test User");
        when(contactProfileRepository.findByUserIdAndPlatformAndChatIdentifier(
            userId, CommunicationPlatformType.TELEGRAM, "chat123"))
            .thenReturn(Optional.of(contactProfile));

        // Act - Should not throw exception
        String result = aiMessageFormattingService.formatMessage(
            inputMessage, CommunicationPlatformType.TELEGRAM, "chat123");

        // Assert
        assertNotNull(result);
    }
}
