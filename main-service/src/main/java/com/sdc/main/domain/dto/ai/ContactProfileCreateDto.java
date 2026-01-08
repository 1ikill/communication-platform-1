package com.sdc.main.domain.dto.ai;

import com.sdc.main.domain.constants.ai.CommunicationPlatformType;
import com.sdc.main.domain.constants.ai.RelationshipType;
import com.sdc.main.domain.constants.ai.ToneStyleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for creating ContactProfile.
 * @since 11.2025
 */
@Data
@Schema(description = "Contact profile create DTO")
public class ContactProfileCreateDto {
    @Schema(description = "Contact name", example = "John")
    @NotBlank(message = "Contact name must not be blank")
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
    @NotBlank(message = "Chat identifier must not be blank")
    private String chatIdentifier;
}
