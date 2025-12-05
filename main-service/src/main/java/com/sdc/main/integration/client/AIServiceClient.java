package com.sdc.main.integration.client;

import com.sdc.main.domain.constants.CommunicationPlatformType;
import com.sdc.main.config.properties.MicroserviceIntegrationProperties;
import lombok.RequiredArgsConstructor;
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
}