package com.sdc.discord.service;

import com.sdc.discord.config.DiscordCredentialsManager;
import com.sdc.discord.config.security.CurrentUser;
import com.sdc.discord.domain.dto.message.ChannelMessageDto;
import com.sdc.discord.domain.dto.message.DiscordPrivateMessageDto;
import com.sdc.discord.domain.exception.BadRequestException;
import com.sdc.discord.domain.exception.NotFoundException;
import com.sdc.discord.domain.mapper.ChannelMessageMapper;
import com.sdc.discord.domain.mapper.DiscordPrivateMessageMapper;
import com.sdc.discord.domain.model.DiscordCredentials;
import com.sdc.discord.domain.model.DiscordPrivateMessage;
import com.sdc.discord.repository.DiscordCredentialsRepository;
import com.sdc.discord.repository.DiscordPrivateMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.isNull;

/**
 * Service for Discord message managing.
 * @since 12.2025
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DiscordMessageService {
    private static final int CHANNEL_SEARCH_DEPTH_LIMIT = 100;
    private static final int MAX_FILES_PER_MESSAGE = 10;
    private static final int MAX_FILES_PER_MESSAGE_BYTE_SIZE = 6291456;

    private final DiscordPrivateMessageRepository messageRepository;
    private final DiscordCredentialsRepository credentialsRepository;
    private final DiscordCredentialsManager credentialsManager;
    private final DiscordPrivateMessageMapper mapper;
    private final ChannelMessageMapper channelMessageMapper;
    private final CurrentUser currentUser;

    /**
     * Send direct message to user.
     * @param botId current botId.
     * @param targetUserId target user id.
     * @param message message text.
     */
    public void sendDirectMessage(final Long botId, final String targetUserId, final String message) {
        final JDA jda = getJda(botId);

        final User targetUser = jda.getUserById(targetUserId);
        if (targetUser == null) {
            throw new NotFoundException("User not found");
        }

        if (targetUser.isBot()) {
            throw new BadRequestException("It is not allowed to send messages to bots.");
        }

        targetUser.openPrivateChannel()
                .flatMap(channel -> channel.sendMessage(message)
                );
    }

    /**
     * Send guild channel message.
     * @param botId currentBotId.
     * @param channelId guild channel id.
     * @param text message text.
     */
    public void sendChannelMessage(final Long botId, final String channelId, final String text) {
        final JDA jda = getJda(botId);

        final TextChannel channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            throw new NotFoundException("Channel is not found");
        }

        channel.sendMessage(text);
    }

    /**
     * Send file message to guild channel.
     * @param botId current bot id.
     * @param channelId guild channel id.
     * @param files files to send.
     * @param message message text to send.
     */
    public void sendChannelFileMessage(final Long botId, final String channelId, final List<MultipartFile> files, final String message) throws IOException {
        final JDA jda = getJda(botId);

        final TextChannel channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            throw new NotFoundException("Channel is not found");
        }

        final List<FileUpload> fileUploads = createFileUpload(files);
        sendFileMessage(message, fileUploads, channel.sendMessage(message), channel.sendMessage(""));
    }

    /**
     * Send direct file message.
     * @param botId current bot id.
     * @param userId target user id.
     * @param files files to send.
     * @param message message text to send.
     */
    public void sendDirectFileMessage(final Long botId, final String userId, final List<MultipartFile> files, final String message) throws IOException {
        final JDA jda = getJda(botId);

        User targetUser = jda.getUserById(userId);
        if (targetUser == null) {
            throw new NotFoundException("User not found");
        }

        final List<FileUpload> fileUploads = createFileUpload(files);
        final PrivateChannel channel = targetUser.openPrivateChannel().complete();
        sendFileMessage(message, fileUploads, channel.sendMessage(message), channel.sendMessage(""));
    }

    /**
     * Get guild channel message history.
     * @param botId current bot id.
     * @param channelId guild channel id.
     * @param limit messages limit.
     * @return ChannelMessageDto list.
     */
    public List<ChannelMessageDto> getGuildChannelHistory(final Long botId, final String channelId, final int limit) {
        if (!credentialsRepository.existsByUserIdAndId(currentUser.getId(), botId)) {
            throw new BadRequestException("No such bot for user:" + currentUser.getUsername());
        }
        final DiscordCredentials bot = credentialsRepository.findById(botId).orElseThrow(() -> new NotFoundException("Credentials not found"));
        final JDA jda = credentialsManager.getJda(bot);

        if (jda == null) {
            throw new RuntimeException("Bot is not initialized");
        }

        final TextChannel channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            throw new NotFoundException("Channel is not found");
        }

        final List<Message> messages = channel.getHistory().retrievePast(limit).complete();
        final List<ChannelMessageDto> result = new ArrayList<>();
        for (Message message : messages) {
            final Boolean isSelf = Objects.equals(message.getAuthor().getId(), bot.getBotUserId());
            final List<Message.Attachment> attachments = message.getAttachments();
            result.add(channelMessageMapper.toDto(message, message.getTimeCreated(), isSelf, attachments));
        }
        return result;
    }

    /**
     * Delete private messages.
     * @param botId current bot id.
     * @param messageIds message ids to delete.
     */
    @Transactional
    public void deleteMessages(final Long botId, final List<Long> messageIds)  {
        final List<DiscordPrivateMessage> messages = messageRepository.findAllByIdIn(messageIds);

        for (DiscordPrivateMessage message : messages) {
            if (!Objects.equals(message.getBotId(), botId)) {
                continue;
            }
            deleteMessageInDiscord(message.getChannelId(), message.getDiscordMessageId(), botId);
            messageRepository.delete(message);
        }
    }

    /**
     * Delete guild channel messages.
     * @param botId current bot id.
     * @param channelId guild channel id.
     * @param messageIds message ids to delete.
     */
    public void deleteChannelMessages(final Long botId, final String channelId, final List<String> messageIds)  {
        DiscordCredentials bot = credentialsRepository.findById(botId).orElseThrow();
        JDA jda = credentialsManager.getJda(bot);

        if (jda == null) {
            throw new RuntimeException("Bot is not initialized");
        }

        final TextChannel channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            throw new NotFoundException("Channel is not found");
        }
        for (String messageId : messageIds) {
            channel.retrieveMessageById(messageId)
                    .submit()
                    .thenAccept(message -> {
                        if (message.getAuthor().getId().equals(bot.getBotUserId())) {
                            message.delete().submit();
                        }
                    })
                    .exceptionally(throwable -> {
                        log.error("Failed to delete guild message: {}", throwable.getMessage());
                        throw new RuntimeException("Failed to delete message in Discord", throwable);
                    })
                    .join();
        }
    }

    /**
     * Update guild channel message.
     * @param botId current bot id.
     * @param channelId guild channel id.
     * @param messageId message to update id.
     * @param updatedMessage updated message text.
     */
    public void updateChannelMessage(final Long botId, final String channelId, final String messageId, final String updatedMessage) {
        if (updatedMessage == null || updatedMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        if (!credentialsRepository.existsByUserIdAndId(currentUser.getId(), botId)) {
            throw new BadRequestException("No such bot for user:" + currentUser.getUsername());
        }
        final DiscordCredentials bot = credentialsRepository.findById(botId).orElseThrow(() -> new NotFoundException("Credentials not found"));
        final JDA jda = credentialsManager.getJda(bot);

        if (jda == null) {
            throw new RuntimeException("Bot is not initialized");
        }

        final TextChannel channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            throw new NotFoundException("Channel is not found");
        }

        channel.retrieveMessageById(messageId)
                .submit()
                .thenApply(message -> {
                    if (message.getAuthor().getId().equals(bot.getBotUserId())) {
                        return message.editMessage(updatedMessage).submit().join();
                    }
                    return null;
                })
                .exceptionally(throwable -> {
                    log.error("Failed to update guild message: {}", throwable.getMessage());
                    throw new RuntimeException("Failed to update message in Discord", throwable);
                })
                .join();
    }

    /**
     * Update private message.
     * @param botId current bot id.
     * @param messageId message to update id.
     * @param newContent updated message text.
     */
    @Transactional
    public void updateMessage(final Long botId, final Long messageId, final String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }

        final DiscordPrivateMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message is not found"));

        if (!message.getBotId().equals(botId)) {
            throw new BadRequestException("Message does not belong to this bot");
        }

        updateMessageInDiscord(message.getChannelId(), message.getDiscordMessageId(), newContent, botId);

        message.setContent(newContent);
        messageRepository.save(message);
    }

    /**
     * Get private message history.
     * @param botId current bot id.
     * @param channelId private message channel id.
     * @param limit messages limit
     * @return DiscordPrivateMessageDto list.
     */
    public List<DiscordPrivateMessageDto> getPrivateMessageHistory(final Long botId, final String channelId, final int limit) {
        final List<DiscordPrivateMessage> messages = messageRepository.findLastMessages(botId, channelId, limit);

        return messages.stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Message search by search query.
     * @param botId current bot id.
     * @param channelId private message channel id.
     * @param query search query.
     * @return DiscordPrivateMessageDto list of matched results.
     */
    public List<DiscordPrivateMessageDto> searchMessages(final Long botId, final String channelId, final String query) {
        final List<DiscordPrivateMessage> messages = messageRepository.searchByContentNative(
                botId, channelId, query
        );

        return messages.stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Guild channel message search by search query.
     * @param botId current bot id.
     * @param channelId private message channel id.
     * @param query search query.
     * @return ChannelMessageDto list of matched results.
     */
    public List<ChannelMessageDto> searchGuildChannelMessages(final Long botId, final String channelId, final String query) {
        final List<ChannelMessageDto> messages = getGuildChannelHistory(botId, channelId, CHANNEL_SEARCH_DEPTH_LIMIT);

        return messages.stream()
                .filter(obj -> obj.getContent() != null &&
                        obj.getContent().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }

    /**
     * Helper method to delete message from Discord API.
     * @param channelId guild channel id.
     * @param discordMessageId Discord message id.
     * @param botId current bot id.
     */
    private void deleteMessageInDiscord(final String channelId, final String discordMessageId, final Long botId) {
        final JDA jda = getJda(botId);

        final PrivateChannel channel = jda.getPrivateChannelById(channelId);
        if (channel == null) {
            throw new RuntimeException("Channel is not found");
        }

        channel.retrieveMessageById(discordMessageId)
                .submit()
                .thenAccept(message -> message.delete().submit())
                .exceptionally(throwable -> {
                    log.error("Failed to delete message in Discord: {}", throwable.getMessage());
                    throw new RuntimeException("Failed to delete message in Discord", throwable);
                })
                .join();
    }

    /**
     * Helper method to update message in Discord API.
     * @param channelId guild channel id.
     * @param discordMessageId Discord message id.
     * @param newContent updated message text.
     * @param botId current bot id.
     */
    private void updateMessageInDiscord(final String channelId, final String discordMessageId, final String newContent, final Long botId) {
        final JDA jda = getJda(botId);

        final PrivateChannel channel = jda.getPrivateChannelById(channelId);
        if (channel == null) {
            throw new RuntimeException("Channel is not found");
        }

        channel.retrieveMessageById(discordMessageId)
                .submit()
                .thenAccept(message -> message.editMessage(newContent).submit())
                .exceptionally(throwable -> {
                    log.error("Failed to update message in Discord: {}", throwable.getMessage());
                    throw new RuntimeException("Failed to update message in Discord", throwable);
                })
                .join();
    }

    /**
     * Helper method to create file upload request.
     * @param files MultipartFile list.
     * @return FileUpload request list.
     */
    private List<FileUpload> createFileUpload(final List<MultipartFile> files) throws IOException {
        if (files.size() > MAX_FILES_PER_MESSAGE) {
            throw new IllegalArgumentException(
                    String.format("Max files in one request: %d",
                            MAX_FILES_PER_MESSAGE)
            );
        }

        final long totalSize = files.stream()
                .mapToLong(MultipartFile::getSize)
                .sum();

        if (totalSize > MAX_FILES_PER_MESSAGE_BYTE_SIZE) { // 8MB
            throw new IllegalArgumentException(
                    String.format("Total files size is more then 8MB. Current size: %.2f MB",
                            totalSize / (1024.0 * 1024.0))
            );
        }

        final List<FileUpload> fileUploads = new ArrayList<>();

        for (MultipartFile file : files) {
            final FileUpload fileUpload = FileUpload.fromData(
                    file.getInputStream(),
                    file.getOriginalFilename()
            );
            fileUploads.add(fileUpload);
        }

        return fileUploads;
    }

    /**
     * Helper method to send file message.
     * @param message message text.
     * @param fileUploads file upload request.
     * @param messageCreateAction message create action.
     * @param emptyMessageCreateAction empty message create action.
     */
    private void sendFileMessage(final String message, final List<FileUpload> fileUploads, final MessageCreateAction messageCreateAction, final MessageCreateAction emptyMessageCreateAction) {
        MessageCreateAction messageAction;
        if (message != null && !message.isEmpty()) {
            messageAction = messageCreateAction;
        } else {
            messageAction = emptyMessageCreateAction;
        }

        for (FileUpload fileUpload : fileUploads) {
            messageAction = messageAction.addFiles(fileUpload);
        }

        CompletableFuture<Message> future = messageAction.submit();
        final Message sentMessage = future.join();

        if (isNull(sentMessage)) {
            throw new RuntimeException("Failed to send file message");
        }
    }

    /**
     * Helper method to get Discord JDA.
     * @param botId current bot id.
     * @return Discord JDA.
     */
    private JDA getJda(final Long botId) {
        if (!credentialsRepository.existsByUserIdAndId(currentUser.getId(), botId)) {
            throw new BadRequestException("No such bot for user:" + currentUser.getUsername());
        }
        final DiscordCredentials bot = credentialsRepository.findById(botId).orElseThrow(() -> new NotFoundException("Credentials not found"));
        final JDA jda = credentialsManager.getJda(bot);

        if (jda == null) {
            throw new RuntimeException("Bot is not initialized");
        }
        return jda;
    }

}
