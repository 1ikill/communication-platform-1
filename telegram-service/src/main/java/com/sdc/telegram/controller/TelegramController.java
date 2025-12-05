package com.sdc.telegram.controller;

import com.sdc.telegram.domain.dto.TelegramNotificationDto;
import com.sdc.telegram.domain.dto.tdlib.TelegramChatFolderDto;
import com.sdc.telegram.domain.dto.tdlib.chat.ChatTdlibDto;
import com.sdc.telegram.domain.dto.tdlib.message.MessageTdlibDto;
import com.sdc.telegram.domain.dto.tdlib.user.UserTdlibDto;
import com.sdc.telegram.service.TelegramFileService;
import com.sdc.telegram.service.TelegramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.drinkless.tdlib.TdApi;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/telegram")
@RequiredArgsConstructor
public class TelegramController {
    private final TelegramService service;
    private final TelegramFileService fileService;

    @Operation(summary = "Получение списка чатов из основного списка")
    @GetMapping("/main")
    public List<ChatTdlibDto> findAllChatsMain(
            @RequestParam
            final int limit,
            @RequestParam
            final String accountId) throws ExecutionException, InterruptedException {
        log.info("Received request GET /telegram/main request with limit:{}, accountId:{}", limit, accountId);
        final List<ChatTdlibDto> chats = service.findAllChats(limit, new TdApi.ChatListMain(), accountId);
        log.info("Produced response 200 for GET /telegram/main request with body: {}", chats);
        return chats;
    }

    @Operation(summary = "Получение списка чатов из основного списка")
    @GetMapping("/chats/folders")
    public List<TelegramChatFolderDto> findAllChatsFolders(
            @RequestParam
            final String accountId) throws ExecutionException, InterruptedException {
        log.info("Received request GET /telegram/chats/folders request with accountId:{}", accountId);
        final List<TelegramChatFolderDto> folders = service.getChatFolders(accountId);
        log.info("Produced response 200 for GET /telegram/chats/folders request with body: {}", folders);
        return folders;
    }

    @Operation(summary = "Получение списка чатов из основного списка")
    @GetMapping("/folder")
    public List<ChatTdlibDto> findAllChatsFolder(
            @RequestParam
            final int limit,
            @RequestParam
            final int folderId,
            @RequestParam
            final String accountId) throws ExecutionException, InterruptedException {
        log.info("Received request GET /telegram/folder request with limit:{}, folderId:{}, accountId:{}", limit, folderId, accountId);
        final List<ChatTdlibDto> chats = service.findAllChats(limit, new TdApi.ChatListFolder(folderId), accountId);
        log.info("Produced response 200 for GET /telegram/folder request with body: {}", chats);
        return chats;
    }
    @Operation(summary = "Получение списка чатов из архива")
    @GetMapping("/archive")
    public List<ChatTdlibDto> findAllChatsArchive(
            @RequestParam
            final int limit,
            @RequestParam
            final String accountId) throws ExecutionException, InterruptedException {
        log.info("Received request GET /telegram/archive request with limit: {}, accountId:{}", limit, accountId);
        final List<ChatTdlibDto> chats = service.findAllChats(limit, new TdApi.ChatListArchive(), accountId);
        log.info("Produced response 200 for GET /telegram/archive request with body: {}", chats);
        return chats;
    }

    @Operation(summary = "Получение сообщений из чата")
    @GetMapping("/{chatId}/messages")
    public List<MessageTdlibDto> findAllMessages(
            @PathVariable
            final Long chatId,
            @RequestParam
            final int limit,
            @RequestParam
            final String accountId) throws ExecutionException, InterruptedException {
        log.info("Received request GET /telegram/{id}/messages with id:{}, limit:{}, accountId:{}", chatId, limit, accountId);
        final List<MessageTdlibDto> messages = service.findAllMessages(chatId, limit, accountId);
        log.info("Produced response 200 for GET /telegram/{id}/messages request with body: {}", messages);
        return messages;
    }

    @Operation(summary = "Получение сообщения из чата")
    @GetMapping("/{chatId}/message")
    public MessageTdlibDto getTelegramMessage(
            @PathVariable
            final Long chatId,
            @RequestParam
            final Long messageId,
            @RequestParam
            final String accountId) throws ExecutionException, InterruptedException {
        log.info("Received request GET /telegram/{id}/message with id:{}, messageId:{}, accountId:{}", chatId, messageId, accountId);
        final MessageTdlibDto message = service.getTelegramMessage(messageId, chatId, accountId);
        log.info("Produced response 200 for GET /telegram/{id}/message request with body: {}", message);
        return message;
    }

    @Operation(summary = "Получение информации о подключенном аккаунте телеграм")
    @GetMapping("/account")
    public UserTdlibDto getAccountInfo(@RequestParam final String accountId) throws ExecutionException, InterruptedException {
        log.info("Received request GET /telegram/account with accountId:{}", accountId);
        final UserTdlibDto result = service.getAccountInfo(accountId);
        log.info("Produced response 200 for GET /telegram/account?{} request with body: {}", accountId, result);
        return result;
    }

//    @Operation(summary = "Получение списка подключенных акканутов телеграм")
//    @GetMapping("/accounts")
//    public List<TelegramAccountDto> getAllAccountsInfo() throws ExecutionException, InterruptedException {
//        log.info("Received request GET /telegram/accounts");
//        final List<TelegramAccountDto> result = service.getAllAccountsInfo();
//        log.info("Produced response 200 for GET /telegram/accounts request with body: {}", result);
//        return result;
//    }

    @Operation(summary = "Отправление сообщения")
    @PostMapping("/text")
    public void sendTextMessage(
            @RequestParam
            final Long chatId,
            @RequestParam
            final String messageText,
            @RequestParam
            final String accountId) throws ExecutionException, InterruptedException {
        log.info("Received request POST /telegram/text request with chatId:{}, text:{}, accountId:{}", chatId, messageText, accountId);
        service.sendTextMessage(chatId, messageText, accountId);
        log.info("Produced response 200 for POST /telegram/text request");
    }

    @Operation(summary = "Отправление сообщения")
    @PostMapping("/image")
    public void sendImageMessage(
            @RequestParam
            final Long chatId,
            @RequestPart
            final MultipartFile image,
            @RequestParam(required = false)
            String message,
            @RequestParam
            final String accountId) {
        log.info("Received request POST /telegram/image request with chatId:{}, message:{}, accountId:{}", chatId, message, accountId);
        service.sendImageMessage(chatId, image, message, accountId);
        log.info("Produced response 200 for POST /telegram/image request");
    }

    @Operation(summary = "Смена фото профиля")
    @PostMapping("/profile/images")
    public void setProfileImage(
            @RequestPart
            final MultipartFile image,
            @RequestParam
            final String accountId)
    {
        log.info("Received request POST /telegram/profile/image with accountId:{}", accountId);
        service.setProfilePhoto(image, accountId);
        log.info("Produced response 200 for POST /telegram/profile/image request");
    }

    @Operation(summary = "Отправление сообщения")
    @PostMapping("/videos")
    public void sendVideoMessage(
            @RequestParam
            final Long chatId,
            @RequestPart
            final MultipartFile video,
            @RequestParam(required = false)
            String message,
            @RequestParam
            final String accountId) {
        log.info("Received request POST /telegram/video request with chatId:{}, message:{}, accountId:{}", chatId, message, accountId);
        service.sendVideoMessage(chatId, video, message, accountId);
        log.info("Produced response 200 for POST /telegram/video request");
    }

    @Operation(summary = "Отправление документа")
    @PostMapping("/documents")
    public void sendDocumentMessage(
            @RequestParam
            final Long chatId,
            @RequestPart
            final MultipartFile document,
            @RequestParam(required = false)
            String message,
            @RequestParam
            final String accountId) {
        log.info("Received request POST /telegram/document request with chatId:{}, message:{}, accountId:{}", chatId, message, accountId);
        service.sendDocumentMessage(chatId, document, message, accountId);
        log.info("Produced response 200 for POST /telegram/document request");
    }

    @Operation(summary = "Получение чата с пользователем по имени пользователя")
    @GetMapping("/user-chat")
    public Long getUserChatId(
            @RequestParam
            final String username,
            @RequestParam
            final String accountId) throws ExecutionException, InterruptedException {
        log.info("Received request GET /telegram/user-chat request with username: {}, accountId:{}", username, accountId);
        final Long userId = service.findUserChatIdByUsername(username, accountId);
        log.info("Produced response 200 for GET /telegram/user-chat request with body:{}", userId);
        return userId;
    }

    @Operation(summary = "Создание чата")
    @PostMapping("/create-chat/{userId}")
    public Long createChat(
            @PathVariable
            final Long userId,
            @RequestParam
            final String accountId) throws ExecutionException, InterruptedException {
        log.info("Received request POST /telegram/create-chat/{userId} request with userId: {}, accountId:{}", userId, accountId);
        final Long chatId = service.createChatWithUser(userId, accountId);
        log.info("Produced response 200 for POST /telegram/create-chat/{userId} request with body:{}", chatId);
        return chatId;
    }

    @Operation(description = "Получение изображения из сообщения")
    @GetMapping(value = "/file/{remoteId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getTelegramImage(
            @PathVariable
            @Parameter(description = "Идентификатор файла")
            final String remoteId,
            @RequestParam
            @Parameter(description = "Идентфикатор аккаунта")
            final String accountId) throws IOException, ExecutionException, InterruptedException {
        log.info("Received request GET telegram/file/{} with accountId:{}", remoteId, accountId);
        final byte[] result = fileService.getTelegramImage(remoteId, accountId);
        log.info("Produced response 200 for GET telegram/file/{} request", remoteId);
        return result;
    }

    @Operation(description = "Получение видео из сообщения")
    @GetMapping(value = "/video/{remoteId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public StreamingResponseBody getTelegramVideo(
            @PathVariable
            @Parameter(description = "Идентификатор файла")
            final String remoteId,
            @RequestParam
            @Parameter(description = "Идентификатор аккаунта")
            final String accountId) throws IOException, ExecutionException, InterruptedException {

        log.info("Received request GET telegram/video/{} with accountId:{}", remoteId, accountId);
        final StreamingResponseBody result = fileService.getTelegramVideo(remoteId, accountId);
        log.info("Produced response 200 for GET telegram/video/{} request", remoteId);
        return result;
    }

    @Operation(description = "Создание пустого чата с пользователем")
    @PostMapping("/empty-chats")
    public void createEmptyChat(@RequestParam final Long chatId, @RequestParam final String accountId) {
        log.info("Received request POST /empty-chats request with chatId:{}, accountId:{}", chatId, accountId);
        service.createEmptyChat(chatId, accountId);
        log.info("Produced response 200 for POST /empty-chats request");
    }

    @Operation(description = "Удаление пустого чата с пользователем")
    @DeleteMapping("/empty-chats")
    public void deleteEmptyChat(@RequestParam final Long chatId, @RequestParam final String accountId) {
        log.info("Received request DELETE /empty-chats request with chatId:{}, accountId:{}", chatId, accountId);
        service.deleteEmptyChat(chatId, accountId);
        log.info("Produced response 200 for DELETE /empty-chats request");
    }


    @Operation(description = "Получение документа из сообщения")
    @GetMapping(value = "/document/{remoteId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public StreamingResponseBody getTelegramDocument(
            @PathVariable
            @Parameter(description = "Идентификатор файла")
            final String remoteId,
            @RequestParam
            @Parameter(description = "Идентификатор аккаунта")
            final String accountId) throws IOException, ExecutionException, InterruptedException {

        log.info("Received request GET telegram/document/{} with accountId:{}", remoteId, accountId);
        final StreamingResponseBody result = fileService.getTelegramDocument(remoteId, accountId);
        log.info("Produced response 200 for GET telegram/document/{} request", remoteId);
        return result;
    }

    @Operation(summary = "Получение уведомлений чатов аккаунта")
    @GetMapping("/notifications")
    public List<TelegramNotificationDto> findChatsNotifications(@RequestParam final String accountId) throws ExecutionException, InterruptedException {
        log.info("Received request GET /notifications request with accountId:{}", accountId);
        final List<TelegramNotificationDto> result = service.findChatsNotifications(accountId);
        log.info("Produced response 200 for GET /notifications request with body:{}", result);
        return result;
    }

//    @Operation(summary = "Получение списка чатов из основного списка")
//    @GetMapping("/user")
//    public UserTdlibDto findAllChatsFolder(
//            @RequestParam
//            final Long userId,
//            @RequestParam
//            final String accountId) throws ExecutionException, InterruptedException {
//        log.info("Received request GET /telegram/user request with userId:{}, accountId:{}", userId, accountId);
//        final UserTdlibDto user = service.getUser(accountId, userId);
//        log.info("Produced response 200 for GET /telegram/user request with body: {}", user);
//        return user;
//    }
}
