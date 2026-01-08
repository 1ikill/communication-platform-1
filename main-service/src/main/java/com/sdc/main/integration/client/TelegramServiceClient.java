package com.sdc.main.integration.client;

import com.sdc.main.config.properties.MicroserviceIntegrationProperties;
import com.sdc.main.domain.dto.telegram.TelegramAccountDto;
import com.sdc.main.domain.dto.telegram.TelegramChatFolderDto;
import com.sdc.main.domain.dto.telegram.TelegramCredentialsCreateDto;
import com.sdc.main.domain.dto.telegram.TelegramNotificationDto;
import com.sdc.main.domain.dto.telegram.auth.AuthorizationStateTdlib;
import com.sdc.main.domain.dto.telegram.chat.ChatTdlibDto;
import com.sdc.main.domain.dto.telegram.message.MessageTdlibDto;
import com.sdc.main.domain.dto.telegram.user.UserTdlibDto;
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

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

/**
 * WebClient for telegram-service.
 * @since 11.2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramServiceClient {
    private final static String CHAT_ID_PARAM= "chatId";
    private final static String MESSAGE_TEXT_PARAM= "messageText";
    private final static String ACCOUNT_ID_PARAM= "accountId";
    private final static String LIMIT_PARAM = "limit";
    private final static String MESSAGE_PARAM = "message";
    private final static String MESSAGE_ID_PARAM = "messageId";
    private final static String USERNAME_PARAM = "username";
    private final static String PASSWORD_PARAM = "password";
    private final static String PHONE_PARAM = "phone";
    private final static String CODE_PARAM = "code";
    private final static String FOLDER_ID_PARAM = "folderId";

    private final WebClient webClient;
    private final MicroserviceIntegrationProperties properties;

    public List<ChatTdlibDto> findAllChatsMain(final int limit, final String accountId) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getTelegramService().getGetChatsMainUrl())
                        .queryParam(LIMIT_PARAM, limit)
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ChatTdlibDto>>() {
                })
                .block();
    }

    public List<ChatTdlibDto> findAllChatsArchive(final int limit, final String accountId) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getTelegramService().getGetChatsArchiveUrl())
                        .queryParam(LIMIT_PARAM, limit)
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ChatTdlibDto>>() {
                })
                .block();
    }

    public List<TelegramChatFolderDto> findAllChatFolders(final String accountId) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getTelegramService().getGetChatFoldersUrl())
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<TelegramChatFolderDto>>() {
                })
                .block();
    }

    public List<ChatTdlibDto> findAllChatsFolder(final int limit, final int folderId, final String accountId) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getTelegramService().getGetChatsFolderUrl())
                        .queryParam(LIMIT_PARAM, limit)
                        .queryParam(FOLDER_ID_PARAM, folderId)
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ChatTdlibDto>>() {
                })
                .block();
    }

    public MessageTdlibDto getTelegramMessage(final Long chatId, final Long messageId, final String accountId) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getTelegramService().getBaseUrl()).path("/" + chatId + "/message")
                        .queryParam(MESSAGE_ID_PARAM, messageId)
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .build()
                .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<MessageTdlibDto>() {
                })
                .block();
    }

    public List<MessageTdlibDto> findAllMessages(final Long chatId, final int limit, final String accountId) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getTelegramService().getBaseUrl()).path("/" + chatId + "/messages")
                        .queryParam(CHAT_ID_PARAM, chatId)
                        .queryParam(LIMIT_PARAM, limit)
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .build()
                        .toString())
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(MessageTdlibDto.class)
                .collectList()
                .block();
    }

    public UserTdlibDto getAccountInfo(final String accountId) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getTelegramService().getGetAccountInfoUrl())
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .build()
                .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<UserTdlibDto>() {
                })
                .block();
    }

    public List<TelegramAccountDto> getAllAccountsInfo() {
        return webClient.get()
                .uri(fromHttpUrl(properties.getTelegramService().getGetAccountsInfoUrl())
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<TelegramAccountDto>>() {
                })
                .block();
    }

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

    public void sendImageMessage(final Long chatId, final MultipartFile image, final String message, final String accountId) {
        final MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("image", image.getResource());

        webClient.post()
                .uri(fromHttpUrl(properties.getTelegramService().getSendImageMessageUrl())
                        .queryParam(CHAT_ID_PARAM, chatId)
                        .queryParam(MESSAGE_PARAM, message)
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .build()
                        .toString())
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void sendVideoMessage(final Long chatId, final MultipartFile video, final String message, final String accountId) {
        final MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("video", video.getResource());

        webClient.post()
                .uri(fromHttpUrl(properties.getTelegramService().getSendVideoMessageUrl())
                        .queryParam(CHAT_ID_PARAM, chatId)
                        .queryParam(MESSAGE_PARAM, message)
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .build()
                        .toString())
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void sendDocumentMessage(final Long chatId, final MultipartFile document, final String message, final String accountId) {
        final MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("document", document.getResource());

        webClient.post()
                .uri(fromHttpUrl(properties.getTelegramService().getSendDocumentMessageUrl())
                        .queryParam(CHAT_ID_PARAM, chatId)
                        .queryParam(MESSAGE_PARAM, message)
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .build()
                        .toString())
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public Long getUserChatId(final String username, final String accountId) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getTelegramService().getGetChatIdUrl())
                        .queryParam(USERNAME_PARAM, username)
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Long>() {
                })
                .block();
    }

    public Long createChat(final Long userId, final String accountId) {
        return webClient.post()
                .uri(fromHttpUrl(properties.getTelegramService().getCreateChatUrl())
                        .path(String.valueOf(userId))
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Long>() {
                })
                .block();
    }

    public byte[] getTelegramImage(final String remoteId, final String accountId) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getTelegramService().getGetImageUrl())
                        .path(remoteId)
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<byte[]>() {
                })
                .block();
    }

    public StreamingResponseBody getTelegramVideo(final String remoteId, final String accountId) {
        return outputStream -> {
            webClient.get()
                    .uri(fromHttpUrl(properties.getTelegramService().getGetVideoUrl())
                            .path(remoteId)
                            .queryParam(ACCOUNT_ID_PARAM, accountId)
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

    public StreamingResponseBody getTelegramDocument(final String remoteId, final String accountId){
        return outputStream -> {
            webClient.get()
                    .uri(fromHttpUrl(properties.getTelegramService().getGetDocumentUrl())
                            .path(remoteId)
                            .queryParam(ACCOUNT_ID_PARAM, accountId)
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

    public List<TelegramNotificationDto> findChatsNotifications(final String accountId) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getTelegramService().getGetNotificationsUrl())
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<TelegramNotificationDto>>() {
                })
                .block();
    }

    public void createEmptyChat(final Long chatId, final String accountId) {
        webClient.post()
                .uri(fromHttpUrl(properties.getTelegramService().getCreateEmptyChatUrl())
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .queryParam(CHAT_ID_PARAM, chatId)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void deleteEmptyChat(final Long chatId, final String accountId) {
        webClient.delete()
                .uri(fromHttpUrl(properties.getTelegramService().getDeleteEmptyChatUrl())
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .queryParam(CHAT_ID_PARAM, chatId)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void sendPhoneNumber(final String phone, final String accountId) {
        webClient.post()
                .uri(fromHttpUrl(properties.getTelegramService().getSendPhoneUrl())
                        .queryParam(PHONE_PARAM, phone)
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void sendCode(final String code, final String accountId) {
        webClient.post()
                .uri(fromHttpUrl(properties.getTelegramService().getSendCodeUrl())
                        .queryParam(CODE_PARAM, code)
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void sendPassword(final String password, final String accountId) {
        webClient.post()
                .uri(fromHttpUrl(properties.getTelegramService().getSendPasswordUrl())
                        .queryParam(PASSWORD_PARAM, password)
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public AuthorizationStateTdlib getAuthorizationState(final String accountId) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getTelegramService().getGetAuthStateUrl())
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<AuthorizationStateTdlib>() {
                })
                .block();
    }

    public void logout(final String accountId) {
        webClient.post()
                .uri(fromHttpUrl(properties.getTelegramService().getLogoutUrl())
                        .queryParam(ACCOUNT_ID_PARAM, accountId)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void addCredentials(final TelegramCredentialsCreateDto createDto) {
        webClient.post()
                .uri(fromHttpUrl(properties.getTelegramService().getAddCredentialsUrl())
                        .build()
                        .toString())
                .bodyValue(createDto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
