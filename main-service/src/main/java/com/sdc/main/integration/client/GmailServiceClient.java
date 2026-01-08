package com.sdc.main.integration.client;

import com.sdc.main.config.properties.MicroserviceIntegrationProperties;
import com.sdc.main.domain.dto.gmail.GmailAccountInfoDto;
import com.sdc.main.domain.dto.gmail.GmailMessagesResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

/**
 * WebClient for gmail-service.
 * @since 11.2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GmailServiceClient {
    private final static String CHAT_ID_PARAM= "to";
    private final static String MESSAGE_TEXT_PARAM= "body";
    private final static String ACCOUNT_ID_PARAM= "accountId";
    private final static String SUBJECT_PARAM= "subject";
    private final static String MESSAGE_ID_PARAM= "messageId";
    private final static String PAGE_TOKEN_PARAM= "pageToken";
    private final static String MAX_RESULTS_PARAM= "maxResults";
    private final static String QUERY_PARAM= "query";


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

    public String getAuthUrl(final MultipartFile file) {
        final MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file.getResource());

        return webClient.post()
                .uri(fromHttpUrl(properties.getGmailService().getGetAuthUrl())
                        .build()
                        .toString())
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<String>() {
                })
                .block();
    }

    public void sendFile(final Long accountId, final String to, final String subject, final String body, final MultipartFile file) {
        final MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file.getResource());
        webClient.post()
                .uri(fromHttpUrl(properties.getGmailService().getSendFileUrl())
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .queryParam(CHAT_ID_PARAM, to)
                        .queryParam(SUBJECT_PARAM, subject)
                        .queryParam(MESSAGE_TEXT_PARAM, body)
                        .build()
                        .toString())
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public List<GmailAccountInfoDto> getMe() {
        return webClient.get()
                .uri(fromHttpUrl(properties.getGmailService().getGetMeUrl())
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GmailAccountInfoDto>>() {
                })
                .block();
    }

    public GmailMessagesResponseDto getMessages(final Long accountId, final Integer maxResults, final String pageToken) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getGmailService().getReaderBaseUrl()).path(accountId + "/messages")
                        .queryParam(MAX_RESULTS_PARAM, maxResults)
                        .queryParam(PAGE_TOKEN_PARAM, pageToken)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<GmailMessagesResponseDto>() {
                })
                .block();
    }

    public GmailMessagesResponseDto getUnreadMessage(final Long accountId, final Integer maxResults, final String pageToken) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getGmailService().getReaderBaseUrl()).path(accountId + "/unread")
                        .queryParam(MAX_RESULTS_PARAM, maxResults)
                        .queryParam(PAGE_TOKEN_PARAM, pageToken)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<GmailMessagesResponseDto>() {
                })
                .block();
    }

    public void markAsRead(final Long accountId, String messageId) {
        webClient.post()
                .uri(fromHttpUrl(properties.getGmailService().getReaderBaseUrl()).path(accountId + "/read/" + messageId)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void markAsUnread(final Long accountId, String messageId) {
        webClient.post()
                .uri(fromHttpUrl(properties.getGmailService().getReaderBaseUrl()).path(accountId + "/unread/" + messageId)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public GmailMessagesResponseDto searchMessages(final Long accountId, final String query, final Integer maxResults, final String pageToken) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getGmailService().getReaderBaseUrl()).path(accountId + "/search")
                        .queryParam(QUERY_PARAM, query)
                        .queryParam(MAX_RESULTS_PARAM, maxResults)
                        .queryParam(PAGE_TOKEN_PARAM, pageToken)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<GmailMessagesResponseDto>() {
                })
                .block();
    }

    public StreamingResponseBody downloadImage(final Long accountId, final String messageId, final String attachmentId) {
        return outputStream -> {
            webClient.get()
                    .uri(fromHttpUrl(properties.getGmailService().getReaderBaseUrl()).path(accountId + "/attachment/" + attachmentId + "/image")
                            .queryParam(MESSAGE_ID_PARAM, messageId)
                            .build()
                            .toString())
                    .accept(MediaType.IMAGE_JPEG)
                    .retrieve()
                    .bodyToFlux(DataBuffer.class)
                    .map(dataBuffer -> {
                        try {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            outputStream.write(bytes);
                            return dataBuffer;
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        } finally {
                            DataBufferUtils.release(dataBuffer);
                        }
                    })
                    .doOnError(e -> {
                        try {
                            log.error("Error streaming image", e);
                            outputStream.close();
                        } catch (IOException ex) {
                            log.error("Error closing stream", ex);
                        }
                    })
                    .blockLast();
        };
    }

    public StreamingResponseBody downloadVideo(final Long accountId, final String messageId, final String attachmentId) {
        return outputStream -> {
            webClient.get()
                    .uri(fromHttpUrl(properties.getGmailService().getReaderBaseUrl()).path(accountId + "/attachment/" + attachmentId + "/video")
                            .queryParam(MESSAGE_ID_PARAM, messageId)
                            .build()
                            .toString())
                    .accept(MediaType.APPLICATION_OCTET_STREAM)
                    .retrieve()
                    .bodyToFlux(DataBuffer.class)
                    .map(dataBuffer -> {
                        try {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            outputStream.write(bytes);
                            return dataBuffer;
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        } finally {
                            DataBufferUtils.release(dataBuffer);
                        }
                    })
                    .doOnError(e -> {
                        try {
                            log.error("Error streaming video", e);
                            outputStream.close();
                        } catch (IOException ex) {
                            log.error("Error closing stream", ex);
                        }
                    })
                    .blockLast();
        };
    }

    public StreamingResponseBody downloadDocument(final Long accountId, final String messageId, final String attachmentId) {
        return outputStream -> {
            webClient.get()
                    .uri(fromHttpUrl(properties.getGmailService().getReaderBaseUrl()).path(accountId + "/attachment/" + attachmentId + "/document")
                            .queryParam(MESSAGE_ID_PARAM, messageId)
                            .build()
                            .toString())
                    .accept(MediaType.APPLICATION_OCTET_STREAM)
                    .retrieve()
                    .bodyToFlux(DataBuffer.class)
                    .map(dataBuffer -> {
                        try {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            outputStream.write(bytes);
                            return dataBuffer;
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        } finally {
                            DataBufferUtils.release(dataBuffer);
                        }
                    })
                    .doOnError(e -> {
                        try {
                            log.error("Error streaming document", e);
                            outputStream.close();
                        } catch (IOException ex) {
                            log.error("Error closing stream", ex);
                        }
                    })
                    .blockLast();
        };
    }

}
