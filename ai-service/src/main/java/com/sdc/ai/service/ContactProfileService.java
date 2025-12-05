package com.sdc.ai.service;

import com.sdc.ai.config.security.CurrentUser;
import com.sdc.ai.domain.dto.ContactProfileCreateDto;
import com.sdc.ai.domain.dto.ContactProfileDto;
import com.sdc.ai.domain.dto.ContactProfilePatchDto;
import com.sdc.ai.domain.mapper.ContactProfileMapper;
import com.sdc.ai.domain.model.ContactProfile;
import com.sdc.ai.repository.ContactProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing {@link ContactProfile}.
 * @since 11.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContactProfileService {
    private final ContactProfileRepository repository;
    private final ContactProfileMapper mapper;
    private final CurrentUser currentUser;

    /**
     * Method for contact profile creation.
     * @param createDto - dto for creation of contact profile.
     * @return ContactProfileDto with created contact profile data.
     */
    public ContactProfileDto createContactProfile(final ContactProfileCreateDto createDto) {
        final ContactProfile contactProfile = mapper.fromCreateDto(createDto);
        contactProfile.setUserId(currentUser.getId());
        return mapper.toDto(repository.save(contactProfile));
    }

    /**
     * Method for contact profile patching.
     * @param id contact profile identifier.
     * @param patch ContactProfilePatchDto with patch fields.
     * @return ContactProfileDto with updated contact profile data.
     */
    @Transactional
    public ContactProfileDto patch(final Long id, final ContactProfilePatchDto patch) {
        final ContactProfile contactProfile = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contact profile not found: " + id));
        mapper.merge(contactProfile, patch);
        return mapper.toDto(contactProfile);
    }
}
