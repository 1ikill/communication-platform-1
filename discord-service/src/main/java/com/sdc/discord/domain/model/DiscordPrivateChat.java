package com.sdc.discord.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

/**
 * Discord private chat model.
 * @since 12.2025
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "discord_private_chats", schema = "discord_service")
public class DiscordPrivateChat implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Bot id.
     */
    private Long botId;

    /**
     * Channel id.
     */
    private String channelId;

    /**
     * User id.
     */
    private String userId;

    /**
     * User name.
     */
    private String userName;

    /**
     * User avatar url.
     */
    private String userAvatarUrl;

    /**
     * Last message time.
     */
    private LocalDateTime lastMessageTime;

    /**
     * Last message id.
     */
    private String lastMessageId;

    /**
     * Message count.
     */
    private Integer messageCount;

    /**
     * Created date.
     */
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdDate;

    /**
     * Last modified date.
     */
    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime lastModifiedDate;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
