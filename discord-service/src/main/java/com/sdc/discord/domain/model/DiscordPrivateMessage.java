package com.sdc.discord.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Discord private message model.
 * @since 12.2025
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "discord_private_messages", schema = "discord_service")
public class DiscordPrivateMessage implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Bot id.
     */
    private Long botId;

    /**
     * Discord message id.
     */
    private String discordMessageId;

    /**
     * Discord channel id.
     */
    private String channelId;

    /**
     * Author id.
     */
    private String authorId;

    /**
     * Author name.
     */
    private String authorName;

    /**
     * Message content.
     */
    private String content;

    /**
     * Flag is from bot.
     */
    private Boolean isFromBot;

    /**
     * Flag message has attachments.
     */
    private boolean hasAttachments;

    @OneToMany(mappedBy = "discordMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DiscordMessageFile> files = new ArrayList<>();

    /**
     * Message timestamp.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime timestamp;

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
