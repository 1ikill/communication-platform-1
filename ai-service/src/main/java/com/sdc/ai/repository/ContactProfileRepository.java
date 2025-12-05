package com.sdc.ai.repository;

import com.sdc.ai.domain.constants.CommunicationPlatformType;
import com.sdc.ai.domain.model.ContactProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link ContactProfile}
 * @since 11.2025
 */
@Repository
public interface ContactProfileRepository extends JpaRepository<ContactProfile, Long> {
    Optional<ContactProfile> findByUserIdAndPlatformAndChatIdentifier(final Long userId, final CommunicationPlatformType platform, final String chatIdentifier);
}
