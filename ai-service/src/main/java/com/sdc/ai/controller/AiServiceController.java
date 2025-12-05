package com.sdc.ai.controller;

import com.sdc.ai.domain.constants.CommunicationPlatformType;
import com.sdc.ai.domain.dto.ContactProfileCreateDto;
import com.sdc.ai.domain.dto.ContactProfileDto;
import com.sdc.ai.domain.dto.ContactProfilePatchDto;
import com.sdc.ai.service.AIMessageFormattingService;
import com.sdc.ai.service.ContactProfileService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI=service main controller.
 * @since 11.2025
 */
@Slf4j
@RestController
@RequestMapping("/ai-service")
@RequiredArgsConstructor
public class AiServiceController {
    private final AIMessageFormattingService messageFormattingService;
    private final ContactProfileService profileService;

    /**
     * Method for contact profile creation.
     * @param createDto - dto for creation of contact profile.
     * @return ContactProfileDto with created contact profile data.
     */
    @Operation(description = "Creation of contact profile")
    @PostMapping("profiles/add")
    public ContactProfileDto addProfile(
            @RequestBody
            @Valid
            final ContactProfileCreateDto createDto) {
        log.info("Received request POST /ai-service/profiles/add");
        final ContactProfileDto result = profileService.createContactProfile(createDto);
        log.info("Produced response 200 for POST /ai-service/profiles/add with body:{}", result);
        return result;
    }

    /**
     * Method for contact profile patching.
     * @param id contact profile identifier.
     * @param patch ContactProfilePatchDto with patch fields.
     * @return ContactProfileDto with updated contact profile data.
     */
    @Operation(description = "Patching of contact profile")
    @PatchMapping("profiles/{id}")
    public ContactProfileDto patchProfile(
            @PathVariable
            final Long id,
            @RequestBody
            @Valid
            final ContactProfilePatchDto patch) {
        log.info("Received request PATCH /ai-service/profiles/patch/{}", id);
        final ContactProfileDto result = profileService.patch(id, patch);
        log.info("Produced response 200 for PATCH /ai-service/profiles/patch{} with body:{}", id, result);
        return result;
    }

    /**
     * Method for personalization of generic user message by contact profile.
     * @param platform communication platform.
     * @param chatIdentifier chat identifier.
     * @param message user's generic message to personalize.
     * @return String personalized message.
     */
    @Operation(description = "Message ai-personalization")
    @PostMapping("/format-message")
    public String formatMessage(
            @RequestParam
            final CommunicationPlatformType platform,
            @RequestParam
            final String chatIdentifier,
            @RequestParam
            final String message) {
        log.info("Received request POST /ai-service/format-message with platform:{}, chatIdentifier:{}, message:{}", platform, chatIdentifier, message);
        final String result = messageFormattingService.formatMessage(message, platform, chatIdentifier);
        log.info("Produced response 200 for POST /ai-service/format-message with body:{}", result);
        return result;
    }
}
