package com.sdc.main.domain.dto.ai;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sdc.main.domain.constants.ai.RelationshipType;
import com.sdc.main.domain.constants.ai.ToneStyleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Dto for patching ContactProfile.
 * @since 11.2025
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Dto for patching contact profiles")
public class ContactProfilePatchDto {
    @Schema(description = "Contact name", example = "John")
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
}
