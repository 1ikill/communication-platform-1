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
public class TelegramServiceClient {
    private final static String CHAT_ID_PARAM= "chatId";
    private final static String MESSAGE_TEXT_PARAM= "messageText";
    private final static String ACCOUNT_ID_PARAM= "accountId";

    private final WebClient webClient;
    private final MicroserviceIntegrationProperties properties;

    public void sendTextMessage(final Long chatId, final String messageText, final String accountId){
        webClient.post()
                .uri(fromHttpUrl(properties.getTelegramService().getSendTextMessageUrl())
                        .queryParam(CHAT_ID_PARAM, chatId)
                        .queryParam(MESSAGE_TEXT_PARAM, messageText)
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

}
