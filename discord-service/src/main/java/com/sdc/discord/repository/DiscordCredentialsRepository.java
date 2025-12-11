package com.sdc.discord.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sdc.discord.domain.model.DiscordCredentials;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link DiscordCredentials}.
 * @since 12.2025
 */
@Repository
public interface DiscordCredentialsRepository extends JpaRepository<DiscordCredentials, Long> {
    boolean existsByUserIdAndBotUserId(final Long userId, final String botUserId);

    boolean existsByUserIdAndId(final Long userId, final Long botId);

    List<DiscordCredentials> findAllByIsActive(final Boolean isActive);

    Optional<DiscordCredentials> findByBotUserId(final String userId);

    List<DiscordCredentials> findAllByUserIdAndIsActive(final Long userId, final Boolean isActive);
 }
