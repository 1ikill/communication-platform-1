package com.sdc.telegram.repository;

import com.sdc.telegram.domain.model.TelegramCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TelegramCredentialsRepository extends JpaRepository<TelegramCredentials, Long> {
    Optional<TelegramCredentials> findByAccountId(final String accountId);
}
