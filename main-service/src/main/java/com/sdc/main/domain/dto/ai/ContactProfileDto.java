package com.sdc.main.domain.dto.ai;

import com.sdc.main.domain.constants.ai.CommunicationPlatformType;
import com.sdc.main.domain.constants.ai.RelationshipType;
import com.sdc.main.domain.constants.ai.ToneStyleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for ContactProfile.
 * @since 10.2025
 */
@Data
@Schema(description = "Dto contact profile")
public class ContactProfileDto {
    @Schema(description = "Id")
    private Long id;

    @Schema(description = "User id", example = "2")
    private Long userId;

    @Schema(description = "Contact name", example = "John")
    private String contactName;

    @Schema(description = "Relationship type", example = "SUPERVISOR")
    private RelationshipType relationshipType;

    @Schema(description = "Tone style", example = "FORMAL")
    private ToneStyleType toneStyle;

    @Schema(description = "Formality level", example = "5")
    private Integer formalityLevel;

    @Schema(description = "Preferred greeting", example = "Hello")
    private String preferredGreeting;

    @Schema(description = "Platform", example = "TELEGRAM")
    private CommunicationPlatformType platform;

    @Schema(description = "Chat identifier", example = "11111")
    private String chatIdentifier;

    @Schema(description = "Created date")
    private LocalDateTime createdDate;

    @Schema(description = "Last modified date")
    private LocalDateTime lastModifiedDate;
}
