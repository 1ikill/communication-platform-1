package com.sdc.ai.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdc.ai.domain.constants.CommunicationPlatformType;
import com.sdc.ai.domain.constants.RelationshipType;
import com.sdc.ai.domain.constants.ToneStyleType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Contact profile model.
 * @since 11.2025
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contact_profiles", schema = "ai_service")
public class ContactProfile implements Persistable<Long> {
    /**
     * ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User id.
     */
    private Long userId;

    /**
     * Contact name.
     */
    private String contactName;

    /**
     * Type of relationship of user with contact.
     */
    @Enumerated(EnumType.STRING)
    private RelationshipType relationshipType;

    /**
     * Conversation tone style.
     */
    @Enumerated(EnumType.STRING)
    private ToneStyleType toneStyle;

    /**
     * Level of formality in conversation.
     */
    private Integer formalityLevel;

    /**
     * Preferred greeting.
     */
    private String preferredGreeting;

    /**
     * Platform.
     */
    @Enumerated(EnumType.STRING)
    private CommunicationPlatformType platform;

    /**
     * Chat identifier.
     */
    private String chatIdentifier;

    /**
     * User creation date.
     */
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdDate;

    /**
     * User last modified date.
     */
    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime lastModifiedDate;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
