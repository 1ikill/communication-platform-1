package com.sdc.gmail.domain.model;

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
 * Gmail credentials model.
 * @since 11.2025
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "gmail_credentials", schema = "gmail_service")
public class GmailCredentials implements Persistable<Long> {
    /**
     * Id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User id.
     */
    private Long userId;

    /**
     * Email address.
     */
    private String emailAddress;

    /**
     * Client id.
     */
    private String clientId;

    /**
     * Client Secret.
     */
    private String clientSecret;

    /**
     * Refresh token.
     */
    private String refreshToken;

    /**
     * Access token.
     */
    private String accessToken;

    /**
     * Token expiry.
     */
    private LocalDateTime tokenExpiry;

    /**
     * Scopes.
     */
    private String scopes;

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
