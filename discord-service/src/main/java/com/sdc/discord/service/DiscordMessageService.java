package com.sdc.discord.service;

import com.sdc.discord.config.DiscordCredentialsManager;
import com.sdc.discord.domain.dto.message.DiscordPrivateMessageDto;
import com.sdc.discord.domain.mapper.DiscordPrivateMessageMapper;
import com.sdc.discord.domain.model.DiscordCredentials;
import com.sdc.discord.domain.model.DiscordPrivateMessage;
import com.sdc.discord.repository.DiscordCredentialsRepository;
import com.sdc.discord.repository.DiscordPrivateMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiscordPrivateMessageService {
    private final DiscordPrivateMessageRepository messageRepository;
    private final DiscordCredentialsRepository credentialsRepository;
    private final DiscordCredentialsManager credentialsManager;
    private final DiscordPrivateMessageMapper mapper;

    @Transactional
    public void deleteMessage(Long botId, Long messageId) throws Exception {
        DiscordPrivateMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!Objects.equals(message.getBotId(), botId)) {
            throw new RuntimeException("Message does not belong to this bot");
        }

        deleteMessageInDiscord(message.getChannelId(), message.getDiscordMessageId(), botId);

        messageRepository.delete(message);

        log.info("Deleted message {} for bot {} in Discord and DB", messageId, botId);
    }

    @Transactional
    public void updateMessage(Long botId, Long messageId, String newContent) throws Exception {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new RuntimeException("Content cannot be empty");
        }

        DiscordPrivateMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!message.getBotId().equals(botId)) {
            throw new RuntimeException("Message does not belong to this bot");
        }

        updateMessageInDiscord(message.getChannelId(), message.getDiscordMessageId(), newContent, botId);

        message.setContent(newContent);
        DiscordPrivateMessage updatedMessage = messageRepository.save(message);

        log.info("Updated message {} for bot {} in Discord and DB", messageId, botId);
    }

    private void deleteMessageInDiscord(String channelId, String discordMessageId, Long botId) throws Exception {
        DiscordCredentials botCredentials = credentialsRepository.findById(botId)
                .orElseThrow(() -> new RuntimeException("Bot not found"));
        JDA jda = credentialsManager.getJda(botCredentials);

        try {
            PrivateChannel channel = jda.getPrivateChannelById(channelId);
            if (channel == null) {
                throw new RuntimeException("Channel not found");
            }

            channel.retrieveMessageById(discordMessageId)
                    .submit()
                    .thenAccept(message -> message.delete().submit())
                    .exceptionally(throwable -> {
                        log.error("Failed to delete message in Discord: {}", throwable.getMessage());
                        throw new RuntimeException("Failed to delete message in Discord", throwable);
                    })
                    .join();

        } catch (Exception e) {
            log.error("Error deleting message in Discord: {}", e.getMessage());
            throw new RuntimeException("Failed to delete message in Discord", e);
        }
    }

    private void updateMessageInDiscord(String channelId, String discordMessageId, String newContent, Long botId) throws Exception {
        DiscordCredentials botCredentials = credentialsRepository.findById(botId)
                .orElseThrow(() -> new RuntimeException("Bot not found"));
        JDA jda = credentialsManager.getJda(botCredentials);

        try {
            PrivateChannel channel = jda.getPrivateChannelById(channelId);
            if (channel == null) {
                throw new RuntimeException("Channel not found");
            }

            channel.retrieveMessageById(discordMessageId)
                    .submit()
                    .thenAccept(message -> message.editMessage(newContent).submit())
                    .exceptionally(throwable -> {
                        log.error("Failed to update message in Discord: {}", throwable.getMessage());
                        throw new RuntimeException("Failed to update message in Discord", throwable);
                    })
                    .join();

        } catch (Exception e) {
            log.error("Error updating message in Discord: {}", e.getMessage());
            throw new RuntimeException("Failed to update message in Discord", e);
        }
    }

    /**
     * Получить историю из БД
     */
    public List<DiscordPrivateMessageDto> getMessageHistoryFromDb(Long botId, String channelId, int limit) {
        List<DiscordPrivateMessage> messages = messageRepository.findLastMessages(botId, channelId, limit);

        return messages.stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Поиск сообщений по тексту
     */
    public List<DiscordPrivateMessageDto> searchMessages(Long botId, String channelId, String query) {
        List<DiscordPrivateMessage> messages = messageRepository.searchByContentNative(
                botId, channelId, query
        );

        return messages.stream()
                .map(mapper::toDto)
                .toList();
    }

}
