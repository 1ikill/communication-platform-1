package com.sdc.main.integration.client;

import com.sdc.main.domain.constants.CommunicationPlatformType;
import com.sdc.main.config.properties.MicroserviceIntegrationProperties;
import com.sdc.main.domain.dto.ai.ContactProfileCreateDto;
import com.sdc.main.domain.dto.ai.ContactProfileDto;
import com.sdc.main.domain.dto.ai.ContactProfilePatchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

/**
 * WebClient for AI-service.
 * @since 11.2025
 */
@Component
@RequiredArgsConstructor
public class AIServiceClient {
    private final static String MESSAGE_PARAM = "message";
    private final static String PLATFORM_PARAM = "platform";
    private final static String CHAT_ID_PARAM = "chatIdentifier";

    private final WebClient webClient;
    private final MicroserviceIntegrationProperties properties;

    public String customizeMessage(final String message, final CommunicationPlatformType platform, final String chatIdentifier) {
        return webClient.post()
                .uri(fromHttpUrl(properties.getAiService().getFormatMessageUrl())
                        .queryParam(MESSAGE_PARAM, message)
                        .queryParam(PLATFORM_PARAM, platform)
                        .queryParam(CHAT_ID_PARAM, chatIdentifier)
                        .build().toString())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public ContactProfileDto addContactProfile(final ContactProfileCreateDto createDto) {
        return webClient.post()
                .uri(fromHttpUrl(properties.getAiService().getAddContactProfileUrl())
                        .build()
                        .toString())
                .bodyValue(createDto)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ContactProfileDto>() {
                })
                .block();
    }

    public ContactProfileDto pathProfile(final Long id, final ContactProfilePatchDto patchDto) {
        return webClient.patch()
                .uri(fromHttpUrl(properties.getAiService().getPatchContactProfileUrl()).path(String.valueOf(id))
                        .build()
                        .toString())
                .bodyValue(patchDto)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ContactProfileDto>() {
                })
                .block();
    }



}