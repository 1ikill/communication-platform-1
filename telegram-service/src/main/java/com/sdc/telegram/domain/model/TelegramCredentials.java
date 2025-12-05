package com.sdc.telegram.domain.model;

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
 * Telegram Credentials model.
 * @since 11.2025
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "telegram_credentials", schema = "telegram_service")
public class TelegramCredentials implements Persistable<Long> {
    /**
     * ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Telegram account api_id.
     */
    private String apiId;

    /**
     * Telegram account api_hash.
     */
    private String apiHash;

    /**
     * Account identifier inside of application.
     */
    private String accountId;

    /**
     * Account name inside of application.
     */
    private String accountName;

    /**
     * Telegram account phone number.
     */
    private String phoneNumber;

    /**
     * User id.
     */
    private Long userId;

    /**
     * Telegram credentials creation date.
     */
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdDate;

    /**
     * Telegram credentials last modified date.
     */
    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime lastModifiedDate;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
