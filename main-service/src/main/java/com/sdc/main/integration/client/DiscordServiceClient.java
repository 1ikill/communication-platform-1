package com.sdc.main.integration.client;

import com.sdc.main.config.properties.MicroserviceIntegrationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

/**
 * WebClient for telegram-service.
 * @since 11.2025
 */
@Component
@RequiredArgsConstructor
public class DiscordServiceClient {
    private static final String USER_ID_PARAM = "userId";
    private static final String CHANNEL_ID_PARAM = "channelId";
    private static final String MESSAGE_PARAM = "message";

    private final WebClient webClient;
    private final MicroserviceIntegrationProperties properties;

    public void sendDirectMessage(final String chatId, final String messageText, final Long accountId) {
        webClient.post()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()
                        + accountId
                        + properties.getDiscordService().getSendDirectMessageUrl())
                        .queryParam(USER_ID_PARAM, chatId)
                        .queryParam(MESSAGE_PARAM, messageText)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void sendChannelMessage(final String chatId, final String messageText, final Long accountId) {
        webClient.post()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()
                        + accountId
                        + properties.getDiscordService().getSendChannelMessageUrl())
                        .queryParam(CHANNEL_ID_PARAM, chatId)
                        .queryParam(MESSAGE_PARAM, messageText)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
