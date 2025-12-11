package com.sdc.telegram.repository;

import com.sdc.telegram.domain.model.TelegramCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link TelegramCredentials}
 * @since 12.2025
 */
@Repository
public interface TelegramCredentialsRepository extends JpaRepository<TelegramCredentials, Long> {
    Optional<TelegramCredentials> findByAccountId(final String accountId);

    List<TelegramCredentials> findAllByUserId(final Long userId);
}
