package com.sdc.telegram.service;

import com.sdc.telegram.config.TelegramClientManager;
import com.sdc.telegram.config.properties.TelegramConfigurationProperties;
import com.sdc.telegram.domain.dto.TelegramAccountDto;
import com.sdc.telegram.domain.dto.TelegramNotificationDto;
import com.sdc.telegram.domain.dto.tdlib.TelegramChatFolderDto;
import com.sdc.telegram.domain.dto.tdlib.chat.ChatTdlibDto;
import com.sdc.telegram.domain.dto.tdlib.chat.type.ChatTypeBasicGroupDto;
import com.sdc.telegram.domain.dto.tdlib.chat.type.ChatTypePrivateDto;
import com.sdc.telegram.domain.dto.tdlib.chat.type.ChatTypeSupergroupDto;
import com.sdc.telegram.domain.dto.tdlib.chat.type.ChatTypeTdlib;
import com.sdc.telegram.domain.dto.tdlib.message.MessageTdlibDto;
import com.sdc.telegram.domain.dto.tdlib.user.UserTdlibDto;
import com.sdc.telegram.domain.mapper.chat.ChatTdlibMapper;
import com.sdc.telegram.domain.mapper.message.MessageTdlibMapper;
import com.sdc.telegram.domain.mapper.user.UserTdlibMapper;
import com.sdc.telegram.repository.TelegramCredentialsRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static com.sdc.telegram.domain.constants.PhotoFileType.PHOTO;
import static com.sdc.telegram.domain.constants.PhotoFileType.PROFILE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramService {
    private static final Long TELEGRAM_SERVICE_CHAT_ID = 777000L;
    private final TelegramClientManager clientManager;

    private final ChatTdlibMapper chatMapper;
    private final MessageTdlibMapper messageMapper;
    private final UserTdlibMapper userMapper;

    private final TelegramConfigurationProperties telegramProperties;
    private final TelegramCredentialsRepository credentialsRepository;

    public List<ChatTdlibDto> findAllChats(final int limit, final TdApi.ChatList chatList, final String accountId) throws ExecutionException, InterruptedException {
        final Client client = clientManager.getClient(accountId);
        final List<TdApi.Chat> chats = new ArrayList<>();
        final CompletableFuture<Void> allChatsFuture = new CompletableFuture<>();

        final TdApi.GetChats getChats = new TdApi.GetChats();
        getChats.limit = limit;
        getChats.chatList = chatList;

        client.send(getChats, result -> {
            if (result instanceof TdApi.Chats) {
                final TdApi.Chats chatIds = (TdApi.Chats) result;

                final List<CompletableFuture<TdApi.Chat>> chatFutures = new ArrayList<>();
                for (long chatId : chatIds.chatIds) {
                    chatFutures.add(fetchChatDetailsAsync(chatId, client));
                }

                CompletableFuture.allOf(chatFutures.toArray(new CompletableFuture[0]))
                        .thenAccept(v -> {
                            for (CompletableFuture<TdApi.Chat> future : chatFutures) {
                                try {
                                    chats.add(future.get());
                                } catch (InterruptedException | ExecutionException e) {
                                    throw new RuntimeException("Error fetching chat details", e);
                                }
                            }
                            allChatsFuture.complete(null);
                        })
                        .exceptionally(ex -> {
                            allChatsFuture.completeExceptionally(ex);
                            return null;
                        });
            } else {
                allChatsFuture.completeExceptionally(
                        new RuntimeException("Error fetching chat IDs: " + result));
            }
        });

        allChatsFuture.get();
        final List<ChatTdlibDto> chatTdlibDtos = chatMapper.toDto(chats);
        getProfilePhotos(chatTdlibDtos, accountId);
        return chatTdlibDtos;
    }

    public List<TelegramChatFolderDto> getChatFolders(final String accountId) throws ExecutionException, InterruptedException {
        final Client client = clientManager.getClient(accountId);
        reloadClientCache(client);

        CompletableFuture<List<TdApi.ChatList>> folderIdsFuture = new CompletableFuture<>();

        client.send(new TdApi.GetChatListsToAddChat(TELEGRAM_SERVICE_CHAT_ID), result -> {
            if (result instanceof TdApi.ChatLists lists) {
                folderIdsFuture.complete(Arrays.asList(lists.chatLists));
            } else {
                log.error("Failed to find available chat folders");
                folderIdsFuture.complete(Collections.emptyList());
            }
        });
        final List<TdApi.ChatList> chatLists = folderIdsFuture.get();
        if (chatLists.isEmpty()) {
            return Collections.emptyList();
        }

        final List<Integer> chatFolderIds = folderIdsFuture.get()
                .stream()
                .filter(chatList -> chatList instanceof TdApi.ChatListFolder)
                .map(chatList -> ((TdApi.ChatListFolder) chatList).chatFolderId)
                .toList();

        final List<TelegramChatFolderDto> chatFolders = new ArrayList<>();
        for (Integer chatFolderId : chatFolderIds) {
            CompletableFuture<TdApi.ChatFolder> folderInfoFuture = new CompletableFuture<>();
            client.send(new TdApi.GetChatFolder(chatFolderId), result -> {
                if (result instanceof TdApi.ChatFolder folder) {
                    folderInfoFuture.complete(folder);
                } else {
                    log.info("Failed to fetch folder info for folderId:{}", chatFolderId);
                }
            });
            chatFolders.add(new TelegramChatFolderDto(folderInfoFuture.get().name.text.text, chatFolderId));
        }

        return chatFolders;
    }

    public Long findUserChatIdByUsername(final String username, final String accountId) throws ExecutionException, InterruptedException {
        final Client client = clientManager.getClient(accountId);
        reloadClientCache(client);
        final CompletableFuture<Long> future = new CompletableFuture<>();
        final TdApi.SearchPublicChat search = new TdApi.SearchPublicChat(username);

        client.send(search, result -> {
            if (result instanceof TdApi.Chat) {
                future.complete(((TdApi.Chat) result).id);
            } else {
                future.completeExceptionally(new RuntimeException("User not found"));
            }
        });

        return future.get();
    }

    public MessageTdlibDto getTelegramMessage(final Long messageId, final Long chatId, final String accountId) throws ExecutionException, InterruptedException {
        final Client client = clientManager.getClient(accountId);
        reloadClientCache(client);
        final TdApi.Chat chat = fetchChatDetailsAsync(chatId, client).get();

        final TdApi.GetMessage getMessage = new TdApi.GetMessage(chatId, messageId);
        final CompletableFuture<TdApi.Message> messageFuture = new CompletableFuture<>();
        client.send(getMessage, result -> {
            if (result instanceof TdApi.Message) {
                messageFuture.complete((TdApi.Message) result);
            } else if (result instanceof TdApi.Error){
                messageFuture.completeExceptionally(new RuntimeException("Failed to fetch message with id: " + result));
            }
        });

        final TdApi.Message message = messageFuture.get();
        final MessageTdlibDto result = messageMapper.toDto(message);
        if (message.content instanceof TdApi.MessagePhoto){
            TdApi.PhotoSize bestSize = getPhotoSize(((TdApi.MessagePhoto) message.content).photo.sizes);
            String remoteFileId = bestSize.photo.remote.id;
            if (remoteFileId != null && !remoteFileId.isEmpty()) {
                result.setPhotoRemoteId(PHOTO.getTitle() + remoteFileId);
            }
        } else if (message.content instanceof TdApi.MessageVideo) {
            TdApi.Video video = ((TdApi.MessageVideo) message.content).video;
            String remoteFileId = video.video.remote.id;
            if (remoteFileId != null && !remoteFileId.isEmpty()){
                result.setVideoRemoteId(remoteFileId);
            }
        } else if (message.content instanceof TdApi.MessageDocument) {
            TdApi.Document document = ((TdApi.MessageDocument) message.content).document;
            String remoteFileId = document.document.remote.id;
            if (remoteFileId != null && !remoteFileId.isEmpty()){
                result.setDocumentRemoteId(remoteFileId);
            }
        }

        return result;
    }

    public Long createChatWithUser(final Long userId, final String accountId) throws ExecutionException, InterruptedException {
        final Client client = clientManager.getClient(accountId);
        final CompletableFuture<Long> future = new CompletableFuture<>();
        final TdApi.CreatePrivateChat createChat = new TdApi.CreatePrivateChat(userId, false);

        client.send(createChat, result -> {
            if (result instanceof TdApi.Chat) {
                final TdApi.Chat chat = (TdApi.Chat) result;
                future.complete(chat.id);
            } else {
                future.completeExceptionally(new RuntimeException("Failed to create chat"));
            }
        });
        return future.get();
    }

    public void sendTextMessage(final Long chatId, final String messageText, final String accountId) throws ExecutionException, InterruptedException {
        final Client client = clientManager.getClient(accountId);
        final TdApi.SendMessage sendMessage = new TdApi.SendMessage();
        reloadClientCache(client);
        sendMessage.chatId = chatId;
        sendMessage.options = new TdApi.MessageSendOptions();
        sendMessage.options.disableNotification = false;
        sendMessage.options.fromBackground = false;
        sendMessage.inputMessageContent = new TdApi.InputMessageText(
                new TdApi.FormattedText(messageText, null),
                null,
                true
        );

        client.send(sendMessage, result -> {
            if (result instanceof TdApi.Message) {
                log.info("Message sent successfully:{}", ((TdApi.Message) result).id);
            } else {
                log.error("Failed to send message:{}", result);
            }
        });
    }


    public List<MessageTdlibDto> findAllMessages(final Long chatId, final int limit, final String accountId) throws ExecutionException, InterruptedException {
        final Client client = clientManager.getClient(accountId);
        final List<TdApi.Message> allMessages = new ArrayList<>();
        long fromMessageId = 0;

        while (true) {
            final List<TdApi.Message> messageBatch = fetchMessageBatch(chatId, fromMessageId, limit, client);

            if (messageBatch.isEmpty()) {
                break;
            }
            allMessages.addAll(messageBatch);

            final TdApi.Message lastMessage = messageBatch.get(messageBatch.size() - 1);
            fromMessageId = lastMessage.id;
        }
        final List<MessageTdlibDto> dtos = new ArrayList<>();

        if (!allMessages.isEmpty()){
            readFetchedMessages(allMessages, chatId, client);
            allMessages.forEach(message -> {
                final MessageTdlibDto dto = messageMapper.toDto(message);
                if (message.content instanceof TdApi.MessagePhoto){
                    TdApi.PhotoSize bestSize = getPhotoSize(((TdApi.MessagePhoto) message.content).photo.sizes);
                    String remoteFileId = bestSize.photo.remote.id;
                    if (remoteFileId != null && !remoteFileId.isEmpty()) {
                        dto.setPhotoRemoteId(PHOTO.getTitle() + remoteFileId);
                    }
                } else if (message.content instanceof TdApi.MessageVideo) {
                    TdApi.Video video = ((TdApi.MessageVideo) message.content).video;
                    String remoteFileId = video.video.remote.id;
                    if (remoteFileId != null && !remoteFileId.isEmpty()){
                        dto.setVideoRemoteId(remoteFileId);
                    }
                } else if (message.content instanceof TdApi.MessageDocument) {
                    TdApi.Document document = ((TdApi.MessageDocument) message.content).document;
                    String remoteFileId = document.document.remote.id;
                    if (remoteFileId != null && !remoteFileId.isEmpty()){
                        dto.setDocumentRemoteId(remoteFileId);
                    }
                }
                dtos.add(dto);
            });
            setReadStatusOutbox(dtos, chatId, client);
        }

        return dtos;
    }

    private void setReadStatusOutbox(final List<MessageTdlibDto> dtos, final Long chatId, final Client client) throws ExecutionException, InterruptedException {
        final TdApi.Chat chat = fetchChatDetailsAsync(chatId, client).get();

        if (Objects.isNull(chat.lastMessage) || chat.lastReadOutboxMessageId == 0){
            return;
        }


        CompletableFuture<TdApi.Message> messageFuture = new CompletableFuture<>();
        final TdApi.GetMessage getMessage = new TdApi.GetMessage(chatId, chat.lastReadOutboxMessageId);

        client.send(getMessage, result -> {
            if (result instanceof TdApi.Message) {
                messageFuture.complete((TdApi.Message) result);
            } else {
                messageFuture.completeExceptionally(new RuntimeException("Error fetching message with id: " + chat.lastReadOutboxMessageId));
            }
        });

        final Integer lastReadDate = messageFuture.get().date;

        dtos.stream()
                .filter(MessageTdlibDto::getIsOutgoing)
                .forEach(dto -> dto.setIsRead(dto.getDate() <= lastReadDate));
    }

    private void getProfilePhotos(final List<ChatTdlibDto> chats, final String accountId) throws ExecutionException, InterruptedException {
        final Client client = clientManager.getClient(accountId);
        for (ChatTdlibDto chat : chats) {
            final ChatTypeTdlib type = chat.getType();
            if (type instanceof ChatTypePrivateDto) {
                final Long userId = ((ChatTypePrivateDto) type).getUserId();
                TdApi.GetUser request = new TdApi.GetUser(userId);
                CompletableFuture<TdApi.User> future = new CompletableFuture<>();
                client.send(request, object -> {
                    if (object instanceof TdApi.User) {
                        future.complete((TdApi.User) object);
                    } else {
                        future.completeExceptionally(
                                new RuntimeException("Failed to get user: " + object)
                        );
                    }
                });
                final TdApi.User user = future.get();
                if (Objects.nonNull(user.profilePhoto) && Objects.nonNull(user.profilePhoto.big)){
                    chat.setProfilePhotoUrl(PROFILE.getTitle() + user.profilePhoto.big.remote.id);
                }
            } else if (type instanceof ChatTypeBasicGroupDto) {
                final Long groupId = ((ChatTypeBasicGroupDto) type).getBasicGroupId();
                TdApi.GetBasicGroup request = new TdApi.GetBasicGroup(groupId);
                CompletableFuture<TdApi.BasicGroup> future = new CompletableFuture<>();
                client.send(request, object -> {
                    if (object instanceof TdApi.BasicGroup) {
                        future.complete((TdApi.BasicGroup) object);
                    }  else {
                        future.completeExceptionally(
                                new RuntimeException("Failed to get basic group: " + object)
                        );
                    }
                });
                final TdApi.BasicGroup group = future.get();

                CompletableFuture<TdApi.BasicGroupFullInfo> infoFuture = new CompletableFuture<>();
                final TdApi.GetBasicGroupFullInfo fullInfoRequest = new TdApi.GetBasicGroupFullInfo(groupId);
                client.send(fullInfoRequest, object -> {
                    if (object instanceof TdApi.BasicGroupFullInfo) {
                        infoFuture.complete((TdApi.BasicGroupFullInfo) object);
                    } else {
                        infoFuture.completeExceptionally(
                                new RuntimeException("Failed to get basic group info: " + object)
                        );
                    }
                });
                final TdApi.BasicGroupFullInfo info = infoFuture.get();
                if (Objects.nonNull(info.photo)){
                    chat.setProfilePhotoUrl(PHOTO.getTitle() + getPhotoSize(info.photo.sizes).photo.remote.id);
                }
            } else if (type instanceof ChatTypeSupergroupDto) {
                final Long supergroupId = ((ChatTypeSupergroupDto) type).getSupergroupId();
                TdApi.GetSupergroup request = new TdApi.GetSupergroup(supergroupId);
                CompletableFuture<TdApi.Supergroup> future = new CompletableFuture<>();
                client.send(request, object -> {
                    if (object instanceof TdApi.Supergroup) {
                        future.complete((TdApi.Supergroup) object);
                    } else {
                        future.completeExceptionally(
                                new RuntimeException("Failed to get supergroup: " + object)
                        );
                    }
                });
                final TdApi.Supergroup group = future.get();

                CompletableFuture<TdApi.SupergroupFullInfo> infoFuture = new CompletableFuture<>();
                final TdApi.GetSupergroupFullInfo fullInfoRequest = new TdApi.GetSupergroupFullInfo(supergroupId);
                client.send(fullInfoRequest, object -> {
                    if (object instanceof TdApi.SupergroupFullInfo) {
                        infoFuture.complete((TdApi.SupergroupFullInfo) object);
                    } else {
                        infoFuture.completeExceptionally(
                                new RuntimeException("Failed to get supergroup info: " + object)
                        );
                    }
                });
                final TdApi.SupergroupFullInfo info = infoFuture.get();

                if (Objects.nonNull(info.photo)){
                    chat.setProfilePhotoUrl(PHOTO.getTitle() + getPhotoSize(info.photo.sizes).photo.remote.id);
                }
            }
        }
    }

    public void setProfilePhoto(final MultipartFile file, final String accountId) {
        final Client client = clientManager.getClient(accountId);

        File tempFile;
        try {
            tempFile = convertMultipartFileToFile(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process image", e);
        }

        final TdApi.InputFile inputFile = new TdApi.InputFileLocal(tempFile.getAbsolutePath());
        final TdApi.SetProfilePhoto setProfilePhoto = new TdApi.SetProfilePhoto();
        setProfilePhoto.photo = new TdApi.InputChatPhotoStatic();
        ((TdApi.InputChatPhotoStatic) setProfilePhoto.photo).photo = inputFile;

        client.send(setProfilePhoto, result -> {
            if (result instanceof TdApi.Error) {
                log.error("Failed to set profile photo:{} ", ((TdApi.Error) result).message);
            } else {
                log.info("Profile photo set successfully on account:{}.", accountId);
            }
            try {
                Thread.sleep(350);
                tempFile.delete();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void sendImageMessage(final Long chatId, final MultipartFile file, final String message, final String accountId) {
        final Client client = clientManager.getClient(accountId);
        final TdApi.SendMessage sendMessage = new TdApi.SendMessage();

        File tempFile;
        BufferedImage image;
        try {
            tempFile = convertMultipartFileToFile(file);
            image = ImageIO.read(tempFile);

            BufferedImage resizedImage = resizeIfTooLarge(image);
            if (resizedImage != image) {
                String format = getImageFormat(file.getOriginalFilename());
                ImageIO.write(resizedImage, format, tempFile);
                image = resizedImage;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to process image", e);
        }

        sendMessage.chatId = chatId;
        sendMessage.inputMessageContent = new TdApi.InputMessagePhoto(new TdApi.InputFileLocal(tempFile.getAbsolutePath()), null, null,
                image.getWidth(), image.getHeight(), new TdApi.FormattedText(message, null), false, null, false);

        client.send(sendMessage, result -> {
            if (result instanceof TdApi.Message) {
                log.info("Image message sent successfully:{}", ((TdApi.Message) result).id);
            } else {
                log.error("Failed to send image message:{}", result);
            }
            try {
                Thread.sleep(350);
                tempFile.delete();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void sendVideoMessage(final Long chatId, final MultipartFile file, final String message, final String accountId) {
        final Client client = clientManager.getClient(accountId);

        final TdApi.SendMessage sendMessage = new TdApi.SendMessage();
        File tempFile;
        try {
            tempFile = File.createTempFile("upload-", ".mp4");
            file.transferTo(tempFile);
        }  catch (IOException e) {
            throw new RuntimeException("Failed to process video", e);
        }

        TdApi.InputFileLocal inputFile = new TdApi.InputFileLocal(tempFile.getAbsolutePath());
        sendMessage.chatId = chatId;
        sendMessage.inputMessageContent = new TdApi.InputMessageVideo(inputFile, null, null, 0, 0, 0, false, new TdApi.FormattedText(message, null), false, null, false);

        client.send(sendMessage, result -> {
            if (result instanceof TdApi.Message) {
                log.info("Video message sent successfully:{}", ((TdApi.Message) result).id);
                if (((TdApi.Message) result).content  instanceof TdApi.MessageVideo videoContent) {
                    int fileId = videoContent.video.video.id;
                    pollUntilUploaded(client, fileId, tempFile);
                } else {
                    log.warn("Content was not a video");
                }
            } else {
                log.error("Failed to send video message:{}", result);
                tempFile.delete();
            }
        });

    }

    private void pollUntilUploaded(final Client client, final int fileId, File tempFile) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                int maxTries = 60;
                int delayMs = 1000;

                for (int i = 0; i < maxTries; i++) {
                    TdApi.GetFile getFile = new TdApi.GetFile(fileId);
                    CountDownLatch latch = new CountDownLatch(1);
                    final TdApi.File[] fileRef = new TdApi.File[1];

                    client.send(getFile, response -> {
                        if (response instanceof TdApi.File) {
                            fileRef[0] = (TdApi.File) response;
                        }
                        latch.countDown();
                    });

                    latch.await();

                    if (fileRef[0] != null && fileRef[0].remote != null && fileRef[0].remote.isUploadingCompleted) {
                        boolean deleted = tempFile.delete();
                        log.info("Temp file {} deleted after upload completion: {}", tempFile.getName(), deleted);
                        break;
                    }

                    Thread.sleep(delayMs);
                }
            } catch (Exception e) {
                log.error("Error while checking file upload status", e);
            }
        });
    }

    private BufferedImage resizeIfTooLarge(final BufferedImage image) {
        final int maxDimension = 5000;
        final int width = image.getWidth();
        final int height = image.getHeight();

        if (width <= maxDimension && height <= maxDimension) {
            return image;
        }

        final double scale = Math.min((double) maxDimension / width, (double) maxDimension / height);
        final int newWidth = (int) (width * scale);
        final int newHeight = (int) (height * scale);

        Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.drawImage(scaledImage, 0, 0, null);
        g.dispose();

        return resized;
    }

    private String getImageFormat(final String filename) {
        if (filename == null) return "jpg";
        final String lower = filename.toLowerCase();

        return switch (lower.substring(lower.lastIndexOf('.') + 1)) {
            case "png" -> "png";
            case "bmp" -> "bmp";
            case "gif" -> "gif";
            default -> "jpg";
        };
    }

    public void sendDocumentMessage(final Long chatId, final MultipartFile file, final String message, final String accountId) {
        final Client client = clientManager.getClient(accountId);

        TdApi.SendMessage sendMessage = new TdApi.SendMessage();
        File tempFile;
        try {
            String fileName = Objects.nonNull(file.getOriginalFilename()) ? file.getOriginalFilename() : "document";
            tempFile = new File(System.getProperty("java.io.tmpdir"), fileName);
            file.transferTo(tempFile);
        } catch (IOException e){
            throw new RuntimeException("Failed to process document", e);
        }

        TdApi.InputFile inputFile = new TdApi.InputFileLocal(tempFile.getAbsolutePath());
        TdApi.FormattedText caption = new TdApi.FormattedText(message, null);

        sendMessage.chatId = chatId;
        sendMessage.inputMessageContent = new TdApi.InputMessageDocument(inputFile, null, false, caption);

        client.send(sendMessage, result -> {
            if (result instanceof TdApi.Message) {
                log.info("Message sent successfully:{}", ((TdApi.Message) result).id);
            } else {
                log.error("Failed to send message:{}", result);
            }
            try {
                Thread.sleep(350);
                tempFile.delete();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public List<TelegramNotificationDto> findChatsNotifications(final String accountId) throws ExecutionException, InterruptedException {
        final Client client = clientManager.getClient(accountId);
        final List<TelegramNotificationDto> chatNotificationDtos = new ArrayList<>();
        final CompletableFuture<Void> allChatsFuture = new CompletableFuture<>();

        final TdApi.GetChats getChats = new TdApi.GetChats();
        getChats.limit = 1000;
        getChats.chatList = new TdApi.ChatListMain();

        client.send(getChats, result -> {
            if (result instanceof TdApi.Chats) {
                final TdApi.Chats chatIds = (TdApi.Chats) result;

                final List<CompletableFuture<TdApi.Chat>> chatFutures = new ArrayList<>();
                for (long chatId : chatIds.chatIds) {
                    chatFutures.add(fetchChatDetailsAsync(chatId, client));
                }

                CompletableFuture.allOf(chatFutures.toArray(new CompletableFuture[0]))
                        .thenAccept(v -> {
                            for (CompletableFuture<TdApi.Chat> future : chatFutures) {
                                try {
                                    TdApi.Chat chat = future.get();
                                    TelegramNotificationDto dto = new TelegramNotificationDto(accountId, chat.id, chat.unreadCount);
                                    chatNotificationDtos.add(dto);
                                } catch (InterruptedException | ExecutionException e) {
                                    throw new RuntimeException("Error fetching chat details", e);
                                }
                            }
                            allChatsFuture.complete(null);
                        })
                        .exceptionally(ex -> {
                            allChatsFuture.completeExceptionally(ex);
                            return null;
                        });
            } else {
                allChatsFuture.completeExceptionally(
                        new RuntimeException("Error fetching chat IDs: " + result));
            }
        });

        allChatsFuture.get();
        return chatNotificationDtos;
    }

    public UserTdlibDto getAccountInfo(final String accountId) throws ExecutionException, InterruptedException {
        final Client client = clientManager.getClient(accountId);
        CompletableFuture<TdApi.User> future = new CompletableFuture<>();

        client.send(new TdApi.GetMe(), response -> {
            if (response instanceof TdApi.User) {
                future.complete((TdApi.User) response);
            } else {
                future.completeExceptionally(new RuntimeException("Failed to get account info for " + accountId));
            }
        });

        return userMapper.toDto(future.get(), accountId);
    }

//    public List<TelegramAccountDto> getAllAccountsInfo() throws ExecutionException, InterruptedException {
//        final @NotNull List<TelegramConfigurationProperties.TelegramAccountProperties> accountProperties = telegramProperties.getClients();
//        final List<TelegramAccountDto> accounts = new ArrayList<>();
//        for (TelegramConfigurationProperties.TelegramAccountProperties accountProperty : accountProperties) {
//            if (!isAuthorized(accountProperty.getAccountId())){
//                continue;
//            }
//            final UserTdlibDto user = getAccountInfo(accountProperty.getAccountId());
//            String remote = null;
//            if (Objects.nonNull(user.getProfilePhoto()) && Objects.nonNull(user.getProfilePhoto().getBig())){
//                remote = (PROFILE.getTitle() + user.getProfilePhoto().getBig().getRemote().getId());
//            }
//            accounts.add(new TelegramAccountDto(accountProperty.getAccountId(), accountProperty.getAccountName(), remote));
//        }
//
//        return accounts;
//    }


//    private boolean isAuthorized(final String accountId) {
//        Client client = clientManager.getClient(accountId);
//        if (client == null) return false;
//
//        CompletableFuture<TdApi.AuthorizationState> future = new CompletableFuture<>();
//        client.send(new TdApi.GetAuthorizationState(), response -> {
//            if (response instanceof TdApi.AuthorizationState) {
//                future.complete((TdApi.AuthorizationState) response);
//            } else {
//                future.completeExceptionally(new RuntimeException("Unexpected response"));
//            }
//        });
//
//        try {
//            TdApi.AuthorizationState state = future.get();
//            return state instanceof TdApi.AuthorizationStateReady;
//        } catch (Exception e) {
//            return false;
//        }
//    }

    private List<TdApi.Message> fetchMessageBatch(final Long chatId, final Long fromMessageId, final int limit, final Client client) throws ExecutionException, InterruptedException {
        final CompletableFuture<List<TdApi.Message>> future = new CompletableFuture<>();
        final TdApi.GetChatHistory getChatHistory = createGetChatHistoryRequest(chatId, fromMessageId, limit);

        client.send(getChatHistory, result -> {
            if (result instanceof TdApi.Messages) {
                TdApi.Messages messages = (TdApi.Messages) result;
                future.complete(Arrays.asList(messages.messages));
            } else {
                future.completeExceptionally(
                        new RuntimeException("Error fetching messages for chat ID: " + chatId));
            }
        });

        return future.get();
    }

    private TdApi.GetChatHistory createGetChatHistoryRequest(final Long chatId, final Long fromMessageId, final int limit){
        final TdApi.GetChatHistory getChatHistory = new TdApi.GetChatHistory();
        getChatHistory.chatId = chatId;
        getChatHistory.fromMessageId = fromMessageId;
        getChatHistory.limit = limit;
        getChatHistory.offset = 0;
        getChatHistory.onlyLocal = false;
        return getChatHistory;
    }

    private File convertMultipartFileToFile(final MultipartFile multipartFile) throws IOException {
        final File tempFile = File.createTempFile("upload", multipartFile.getOriginalFilename());
        try (FileOutputStream stream = new FileOutputStream(tempFile)) {
            stream.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    private void readFetchedMessages(final List<TdApi.Message> messages, final Long chatId, final Client client) {
        List<Long> messageIdsToView = messages.stream()
                .filter(msg -> !msg.isOutgoing)
                .map(msg -> msg.id)
                .toList();
        TdApi.ViewMessages viewMessages = new TdApi.ViewMessages();
        viewMessages.chatId = chatId;
        viewMessages.messageIds = messageIdsToView.stream().mapToLong(Long::longValue).toArray();
        viewMessages.forceRead = true;

        client.send(viewMessages, response -> {
            if (response instanceof TdApi.Ok) {
                log.info("Messages marked as viewed.");
            } else {
                log.error("Failed to mark messages as viewed:{}", response);
            }
        });
    }

    private UserTdlibDto getUser(final String accountId, final Long userId) throws ExecutionException, InterruptedException {
        final Client client = clientManager.getClient(accountId);

        TdApi.GetUser request = new TdApi.GetUser(userId);
        CompletableFuture<TdApi.User> future = new CompletableFuture<>();
        client.send(request, object -> {
            if (object instanceof TdApi.User) {
                future.complete((TdApi.User) object);
            } else {
                future.completeExceptionally(
                        new RuntimeException("Failed to get user: " + object)
                );
            }
        });
        return userMapper.toDto(future.get(), accountId);
    }

    private TdApi.PhotoSize getPhotoSize(final TdApi.PhotoSize[] photoSizes) {
        TdApi.PhotoSize bestSize = null;
        for (TdApi.PhotoSize size : photoSizes){
            if (Objects.equals(size.type, "m") || Objects.equals(size.type, "b")){
                bestSize = size;
                break;
            }
        }
        if (Objects.isNull(bestSize)){
            throw new RuntimeException("Can process photo sizes, no suitable size type found.");
        }
        return bestSize;
    }
    private CompletableFuture<TdApi.Chat> fetchChatDetailsAsync(final Long chatId, final Client client) {
        final CompletableFuture<TdApi.Chat> future = new CompletableFuture<>();

        client.send(new TdApi.GetChat(chatId), result -> {
            if (result instanceof TdApi.Chat) {
                future.complete((TdApi.Chat) result);
            } else {
                future.completeExceptionally(
                        new RuntimeException("Failed to fetch chat with ID: " + chatId));
            }
        });

        return future;
    }

    private void reloadClientCache(final Client client) throws ExecutionException, InterruptedException {
        CompletableFuture<TdApi.Chats> chatsFuture = new CompletableFuture<>();
        client.send(new TdApi.GetChats(new TdApi.ChatListMain(), 10), result -> {
            if (result instanceof TdApi.Chats chats) {
                chatsFuture.complete(chats);
            } else {
                throw new RuntimeException("Failed to reload clients cache for accountId: " + clientManager.getAccountIdForClient(client));
            }
        });
        final TdApi.Chats chats = chatsFuture.get();
    }

    public void createEmptyChat(final Long chatId, final String accountId) {
        final Client client = clientManager.getClient(accountId);

        final TdApi.DraftMessage draft = new TdApi.DraftMessage(
                null,
                0,
                new TdApi.InputMessageText(
                        new TdApi.FormattedText("draft", null),
                        null,
                        true
                ),
                0L
        );

        client.send(new TdApi.SetChatDraftMessage(chatId, 0L, draft), ignore -> {
        });

    }

    public void deleteEmptyChat(final Long chatId, final String accountId) {
        final Client client = clientManager.getClient(accountId);
        TdApi.SetChatDraftMessage deleteDraft = new TdApi.SetChatDraftMessage(chatId, 0L, null);
        client.send(deleteDraft, result -> {
        });

    }
}
