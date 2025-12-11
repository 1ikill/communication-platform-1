package com.sdc.discord.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

/**
 * Discord credentials model.
 * @since 12.2025
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "discord_credentials", schema = "discord_service")
public class DiscordCredentials implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User id;
     */
    private Long userId;

    /**
     * Encrypted discord bot token.
     */
    private String botToken;

    /**
     * Bot user id;
     */
    private String botUserId;

    /**
     * Bot username.
     */
    private String botUsername;

    /**
     * Is active flag.
     */
    private Boolean isActive;

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