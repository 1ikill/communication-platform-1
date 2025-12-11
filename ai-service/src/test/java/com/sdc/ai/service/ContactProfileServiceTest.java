package com.sdc.ai.service;

import com.sdc.ai.config.security.CurrentUser;
import com.sdc.ai.domain.dto.ContactProfileCreateDto;
import com.sdc.ai.domain.dto.ContactProfileDto;
import com.sdc.ai.domain.dto.ContactProfilePatchDto;
import com.sdc.ai.domain.mapper.ContactProfileMapper;
import com.sdc.ai.domain.model.ContactProfile;
import com.sdc.ai.repository.ContactProfileRepository;
import jakarta.persistence.EntityNotFoundException;
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
class ContactProfileServiceTest {

    @Mock
    private ContactProfileRepository repository;

    @Mock
    private ContactProfileMapper mapper;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private ContactProfileService contactProfileService;

    private ContactProfile contactProfile;
    private ContactProfileDto contactProfileDto;
    private ContactProfileCreateDto createDto;
    private ContactProfilePatchDto patchDto;

    @BeforeEach
    void setUp() {
        contactProfile = new ContactProfile();
        contactProfile.setId(1L);
        contactProfile.setUserId(100L);
        contactProfile.setContactName("John Doe");

        contactProfileDto = new ContactProfileDto();
        contactProfileDto.setId(1L);
        contactProfileDto.setContactName("John Doe");

        createDto = new ContactProfileCreateDto();

        patchDto = new ContactProfilePatchDto();
    }

    @Test
    void createContactProfile_ShouldCreateAndReturnDto() {
        // Arrange
        when(currentUser.getId()).thenReturn(100L);
        when(mapper.fromCreateDto(any(ContactProfileCreateDto.class))).thenReturn(contactProfile);
        when(repository.save(any(ContactProfile.class))).thenReturn(contactProfile);
        when(mapper.toDto(any(ContactProfile.class))).thenReturn(contactProfileDto);

        // Act
        ContactProfileDto result = contactProfileService.createContactProfile(createDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getContactName());
        
        verify(currentUser, times(1)).getId();
        verify(mapper, times(1)).fromCreateDto(createDto);
        verify(repository, times(1)).save(contactProfile);
        verify(mapper, times(1)).toDto(contactProfile);
    }

    @Test
    void createContactProfile_ShouldSetUserIdFromCurrentUser() {
        // Arrange
        Long expectedUserId = 200L;
        ContactProfile profile = new ContactProfile();
        
        when(currentUser.getId()).thenReturn(expectedUserId);
        when(mapper.fromCreateDto(createDto)).thenReturn(profile);
        when(repository.save(any(ContactProfile.class))).thenReturn(profile);
        when(mapper.toDto(any(ContactProfile.class))).thenReturn(contactProfileDto);

        // Act
        contactProfileService.createContactProfile(createDto);

        // Assert
        verify(currentUser).getId();
        verify(repository).save(argThat(savedProfile -> 
            savedProfile.getUserId().equals(expectedUserId)
        ));
    }

    @Test
    void createContactProfile_ShouldCallMapperFromCreateDto() {
        // Arrange
        when(currentUser.getId()).thenReturn(100L);
        when(mapper.fromCreateDto(createDto)).thenReturn(contactProfile);
        when(repository.save(contactProfile)).thenReturn(contactProfile);
        when(mapper.toDto(contactProfile)).thenReturn(contactProfileDto);

        // Act
        contactProfileService.createContactProfile(createDto);

        // Assert
        verify(mapper).fromCreateDto(createDto);
    }

    @Test
    void patch_WithExistingId_ShouldUpdateAndReturnDto() {
        // Arrange
        Long profileId = 1L;
        when(repository.findById(profileId)).thenReturn(Optional.of(contactProfile));
        doNothing().when(mapper).merge(any(ContactProfile.class), any(ContactProfilePatchDto.class));
        when(mapper.toDto(contactProfile)).thenReturn(contactProfileDto);

        // Act
        ContactProfileDto result = contactProfileService.patch(profileId, patchDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getContactName());
        
        verify(repository, times(1)).findById(profileId);
        verify(mapper, times(1)).merge(contactProfile, patchDto);
        verify(mapper, times(1)).toDto(contactProfile);
    }

    @Test
    void patch_WithNonExistingId_ShouldThrowEntityNotFoundException() {
        // Arrange
        Long nonExistingId = 999L;
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> contactProfileService.patch(nonExistingId, patchDto)
        );
        
        assertTrue(exception.getMessage().contains("Contact profile not found"));
        assertTrue(exception.getMessage().contains(String.valueOf(nonExistingId)));
        
        verify(repository, times(1)).findById(nonExistingId);
        verify(mapper, never()).merge(any(), any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void patch_ShouldCallMapperMerge() {
        // Arrange
        Long profileId = 1L;
        when(repository.findById(profileId)).thenReturn(Optional.of(contactProfile));
        doNothing().when(mapper).merge(contactProfile, patchDto);
        when(mapper.toDto(contactProfile)).thenReturn(contactProfileDto);

        // Act
        contactProfileService.patch(profileId, patchDto);

        // Assert
        verify(mapper).merge(contactProfile, patchDto);
    }

    @Test
    void patch_ShouldNotSaveExplicitly() {
        // Arrange
        Long profileId = 1L;
        when(repository.findById(profileId)).thenReturn(Optional.of(contactProfile));
        doNothing().when(mapper).merge(contactProfile, patchDto);
        when(mapper.toDto(contactProfile)).thenReturn(contactProfileDto);

        // Act
        contactProfileService.patch(profileId, patchDto);

        // Assert
        verify(repository, never()).save(any());
    }

    @Test
    void createContactProfile_WithDifferentUserId_ShouldUseCorrectUserId() {
        // Arrange
        Long userId = 555L;
        ContactProfile newProfile = new ContactProfile();
        
        when(currentUser.getId()).thenReturn(userId);
        when(mapper.fromCreateDto(createDto)).thenReturn(newProfile);
        when(repository.save(any(ContactProfile.class))).thenReturn(newProfile);
        when(mapper.toDto(newProfile)).thenReturn(contactProfileDto);

        // Act
        contactProfileService.createContactProfile(createDto);

        // Assert
        verify(repository).save(argThat(profile -> 
            profile.getUserId().equals(userId)
        ));
    }

    @Test
    void patch_WithDifferentProfile_ShouldUpdateCorrectly() {
        // Arrange
        Long profileId = 5L;
        ContactProfile differentProfile = new ContactProfile();
        differentProfile.setId(profileId);
        differentProfile.setContactName("Jane Smith");
        
        ContactProfileDto differentDto = new ContactProfileDto();
        differentDto.setId(profileId);
        differentDto.setContactName("Jane Smith Updated");
        
        when(repository.findById(profileId)).thenReturn(Optional.of(differentProfile));
        doNothing().when(mapper).merge(differentProfile, patchDto);
        when(mapper.toDto(differentProfile)).thenReturn(differentDto);

        // Act
        ContactProfileDto result = contactProfileService.patch(profileId, patchDto);

        // Assert
        assertNotNull(result);
        assertEquals(profileId, result.getId());
        verify(mapper).merge(differentProfile, patchDto);
    }
}
