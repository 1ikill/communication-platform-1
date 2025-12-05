package com.sdc.ai.domain.dto;

import com.sdc.ai.domain.constants.CommunicationPlatformType;
import com.sdc.ai.domain.constants.RelationshipType;
import com.sdc.ai.domain.constants.ToneStyleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.sdc.ai.domain.model.ContactProfile;

/**
 * DTO for creating {@link ContactProfile}.
 * @since 11.2025
 */
@Data
@Schema(description = "Contact profile create DTO")
public class ContactProfileCreateDto {
    @Schema(description = "Contact name", example = "John")
    @NotBlank
    private String contactName;

    @Schema(description = "Relationship type", example = "SUPERVISOR")
    private RelationshipType relationshipType;

    @Schema(description = "Tone style", example = "FORMAL")
    private ToneStyleType toneStyle;

    @Schema(description = "Formality level", example = "5")
    private Integer formalityLevel;

    @Schema(description = "Preferred greeting", example = "Hello")
    @Size(max = 10, message = "Greeting must be shorter then 10 characters")
    private String preferredGreeting;

    @Schema(description = "Communication platform", example = "TELEGRAM")
    private CommunicationPlatformType platform;

    @Schema(description = "Chat identifier", example = "1111111")
    @NotBlank
    private String chatIdentifier;
}
