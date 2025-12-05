package com.sdc.gmail.repository;

import com.sdc.gmail.domain.model.GmailCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link GmailCredentials}
 * @since 11.2025
 */
@Repository
public interface GmailCredentialsRepository extends JpaRepository<GmailCredentials, Long> {
    Optional<GmailCredentials> findByUserId(final Long userId);
}
