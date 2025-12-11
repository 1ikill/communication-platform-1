package com.sdc.discord.repository;

import com.sdc.discord.domain.model.DiscordPrivateChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link DiscordPrivateChat}
 * @since 12.2025
 */
@Repository
public interface DiscordPrivateChatRepository extends JpaRepository<DiscordPrivateChat, Long> {

    Optional<DiscordPrivateChat> findByBotIdAndChannelId(Long botId, String channelId);

    Optional<DiscordPrivateChat> findByBotIdAndUserId(Long botId, String userId);

    List<DiscordPrivateChat> findByBotIdOrderByLastMessageTimeDesc(Long botId);

    @Query("SELECT c FROM DiscordPrivateChat c WHERE c.botId = :botId " +
            "AND LOWER(c.userName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "ORDER BY c.lastMessageTime DESC")
    List<DiscordPrivateChat> searchByUserName(@Param("botId") Long botId, @Param("query") String query);

    @Modifying
    @Transactional
    @Query("UPDATE DiscordPrivateChat c SET c.messageCount = c.messageCount + 1, " +
            "c.lastMessageTime = :lastMessageTime, c.lastMessageId = :lastMessageId, " +
            "c.lastModifiedDate = CURRENT_TIMESTAMP " +
            "WHERE c.botId = :botId AND c.channelId = :channelId")
    void updateChatStats(@Param("botId") Long botId,
                         @Param("channelId") String channelId,
                         @Param("lastMessageTime") LocalDateTime lastMessageTime,
                         @Param("lastMessageId") String lastMessageId);
}
