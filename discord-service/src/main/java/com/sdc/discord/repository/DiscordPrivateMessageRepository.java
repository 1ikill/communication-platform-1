package com.sdc.discord.repository;

import com.sdc.discord.domain.model.DiscordPrivateMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link DiscordPrivateMessage}
 * @since 12.2025
 */
@Repository
public interface DiscordPrivateMessageRepository extends JpaRepository<DiscordPrivateMessage, Long> {
    Optional<DiscordPrivateMessage> findByDiscordMessageId(final String messageId);

    void deleteByDiscordMessageId(final String messageId);

    List<DiscordPrivateMessage> findAllByIdIn(Collection<Long> messageIds);

    Optional<DiscordPrivateMessage> findByBotIdAndDiscordMessageId(final Long botId, final String messageId);

    @Query("SELECT m FROM DiscordPrivateMessage m WHERE m.botId = :botId AND m.channelId = :channelId ORDER BY m.timestamp DESC")
    List<DiscordPrivateMessage> findByBotIdAndChannelIdOrderByTimestampDesc(
            @Param("botId") Long botId,
            @Param("channelId") String channelId
    );

    @Query("SELECT m FROM DiscordPrivateMessage m WHERE m.botId = :botId AND m.channelId = :channelId ORDER BY m.timestamp DESC")
    Page<DiscordPrivateMessage> findByBotIdAndChannelId(
            @Param("botId") Long botId,
            @Param("channelId") String channelId,
            Pageable pageable
    );

    boolean existsByBotIdAndDiscordMessageId(Long botId, String discordMessageId);

    @Query(value = "SELECT * FROM discord_service.discord_private_messages WHERE bot_id = :botId AND channel_id = :channelId ORDER BY timestamp DESC LIMIT :limit",
            nativeQuery = true)
    List<DiscordPrivateMessage> findLastMessages(
            @Param("botId") Long botId,
            @Param("channelId") String channelId,
            @Param("limit") int limit
    );

    @Modifying
    @Transactional
    @Query("DELETE FROM DiscordPrivateMessage m WHERE m.botId = :botId AND m.timestamp < :olderThan")
    int deleteOlderThan(@Param("botId") Long botId, @Param("olderThan") LocalDateTime olderThan);

    @Query("SELECT DISTINCT m.channelId FROM DiscordPrivateMessage m WHERE m.botId = :botId")
    List<String> findDistinctChannelsByBotId(@Param("botId") Long botId);

    @Query("SELECT m FROM DiscordPrivateMessage m " +
            "WHERE m.botId = :botId " +
            "AND m.channelId = :channelId " +
            "AND LOWER(m.content) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "ORDER BY m.timestamp DESC")
    List<DiscordPrivateMessage> searchByContent(
            @Param("botId") Long botId,
            @Param("channelId") String channelId,
            @Param("query") String query,
            Pageable pageable
    );

    @Query(value = "SELECT * FROM discord_service.discord_private_messages " +
            "WHERE bot_id = :botId " +
            "AND channel_id = :channelId " +
            "AND content ILIKE CONCAT('%', :query, '%') " +
            "ORDER BY timestamp DESC " +
            "LIMIT 100",
            nativeQuery = true)
    List<DiscordPrivateMessage> searchByContentNative(
            @Param("botId") Long botId,
            @Param("channelId") String channelId,
            @Param("query") String query
    );
}