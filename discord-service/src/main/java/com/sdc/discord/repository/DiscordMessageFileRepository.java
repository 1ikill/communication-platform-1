package com.sdc.discord.repository;

import com.sdc.discord.domain.model.DiscordMessageFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link DiscordMessageFile}
 * @since 12.2025
 */
@Repository
public interface DiscordMessageFileRepository extends JpaRepository<DiscordMessageFile, Long> {
}
