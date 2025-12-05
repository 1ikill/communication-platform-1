package com.sdc.main.integration.client;

import com.sdc.main.config.properties.MicroserviceIntegrationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

/**
 * WebClient for gmail-service.
 * @since 11.2025
 */
@Component
@RequiredArgsConstructor
public class GmailServiceClient {
    private final static String CHAT_ID_PARAM= "to";
    private final static String MESSAGE_TEXT_PARAM= "body";
    private final static String ACCOUNT_ID_PARAM= "accountId";
    private final static String SUBJECT_PARAM= "subject";

    private final WebClient webClient;
    private final MicroserviceIntegrationProperties properties;

    public void sendTextMessage(final String chatId, final String messageText, final Long accountId, final String subject){
        webClient.post()
                .uri(fromHttpUrl(properties.getGmailService().getSendTextMessageUrl())
                        .queryParam(CHAT_ID_PARAM, chatId)
                        .queryParam(MESSAGE_TEXT_PARAM, messageText)
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .queryParam(SUBJECT_PARAM, subject)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
