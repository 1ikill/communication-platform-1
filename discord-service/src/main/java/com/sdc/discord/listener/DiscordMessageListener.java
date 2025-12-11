package com.sdc.discord.listener;

import com.sdc.discord.domain.model.DiscordCredentials;
import com.sdc.discord.domain.model.DiscordMessageFile;
import com.sdc.discord.domain.model.DiscordPrivateChat;
import com.sdc.discord.domain.model.DiscordPrivateMessage;
import com.sdc.discord.repository.DiscordCredentialsRepository;
import com.sdc.discord.repository.DiscordMessageFileRepository;
import com.sdc.discord.repository.DiscordPrivateChatRepository;
import com.sdc.discord.repository.DiscordPrivateMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Event listener for Discord messages events.
 * @since 12.2025
 * @see ListenerAdapter
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DiscordMessageListener extends ListenerAdapter {
    @Lazy
    @Autowired
    private DiscordMessageListener self;

    private final DiscordCredentialsRepository credentialsRepository;
    private final DiscordPrivateMessageRepository messageRepository;
    private final DiscordPrivateChatRepository chatRepository;
    private final DiscordMessageFileRepository fileRepository;
    private final Map<Long, DiscordCredentials> botCache = new ConcurrentHashMap<>();

    /**
     * Register bot for event listener.
     * @param bot bot credentials.
     */
    public void registerBot(DiscordCredentials bot) {
        botCache.put(bot.getId(), bot);
        log.debug("Registered bot {} for listening", bot.getBotUsername());
    }

    /**
     * Unregister bot from event listener.
     * @param botId bot id.
     */
    public void unregisterBot(Long botId) {
        botCache.remove(botId);
        log.debug("Unregistered bot {} from listening", botId);
    }

    /**
     * Handle ReceivedMessage event.
     * @param event MessageReceivedEvent object.
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromType(ChannelType.PRIVATE)) {
            return;
        }

        String botUserId = event.getJDA().getSelfUser().getId();
        Optional<DiscordCredentials> botOpt = credentialsRepository.findByBotUserId(botUserId);

        if (botOpt.isEmpty()) {
            return;
        }

        DiscordCredentials bot = botOpt.get();
        Message message = event.getMessage();
        User author = message.getAuthor();

        createOrUpdateChat(bot.getId(), message);
        boolean isFromOurBot = author.getId().equals(bot.getBotUserId());

        try {
            self.saveMessage(bot.getId(), message, isFromOurBot);

            if (isFromOurBot) {
                log.debug("Outgoing DM from bot {} to {}: {}",
                        bot.getBotUsername(),
                        message.getChannel().asPrivateChannel().getUser().getName(),
                        message.getContentDisplay());
            } else {
                log.debug("Incoming DM to bot {} from {}: {}",
                        bot.getBotUsername(),
                        author.getName(),
                        message.getContentDisplay());
            }

        } catch (Exception e) {
            log.error("Error saving DM: {}", e.getMessage(), e);
        }

    }

    /**
     * Handle Discord message update event.
     * @param event MessageUpdateEvent object.
     */
    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        if (!event.isFromType(ChannelType.PRIVATE)) {
            return;
        }

        DiscordCredentials targetBot = findTargetBot(event);
        Message updatedMessage = event.getMessage();

        try {
            self.updateMessageContent(targetBot.getId(), updatedMessage);

            log.debug("Updated DM for bot {}: {}",
                    targetBot.getBotUsername(),
                    updatedMessage.getContentDisplay());

        } catch (Exception e) {
            log.error("Error updating DM: {}", e.getMessage(), e);
        }
    }


    /**
     * Handle Discord channel create event.
     * @param event ChannelCreateEvent object.
     */
    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
        if (!Objects.equals(event.getChannel().getType(), ChannelType.PRIVATE)) {
            return;
        }
        DiscordCredentials targetBot = findTargetBot(event);
        if (targetBot != null) {
            log.debug("Private chat created for bot {} with {}",
                    targetBot.getBotUsername(), event.getChannel().asPrivateChannel().getUser().getName());
        }
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        if (!Objects.equals(event.getChannel().getType(), ChannelType.PRIVATE)) {
            return;
        }
        String messageId = event.getMessageId();
        final DiscordPrivateMessage messageToDelete = messageRepository.findByDiscordMessageId(messageId).orElseThrow();
        if (!messageToDelete.getIsFromBot()) {
            messageRepository.deleteByDiscordMessageId(messageId);
            log.debug("Message with discord messageId:{} was deleted", messageId);
        }
    }

    /**
     * Find event's target bot id.
     * @param event GenericEvent object.
     */
    private DiscordCredentials findTargetBot(GenericEvent event) {
        for (DiscordCredentials bot : botCache.values()) {
            try {
                JDA jda = event.getJDA();
                if (jda.getSelfUser().getId().equals(bot.getBotUserId())) {
                    return bot;
                }
            } catch (Exception e) {
                log.debug("Error checking bot {}: {}", bot.getId(), e.getMessage());
            }
        }
        return null;
    }

    /**
     * Update message content.
     * @param botId target event's bot id.
     * @param discordMessage Discord message to update.
     */
    @Transactional
    public void updateMessageContent(Long botId, Message discordMessage) {
        DiscordPrivateMessage existingMessage = messageRepository
                .findByBotIdAndDiscordMessageId(botId, discordMessage.getId())
                .orElse(null);

        if (existingMessage != null) {
            existingMessage.setContent(discordMessage.getContentRaw());
            messageRepository.save(existingMessage);
        }
    }

    /**
     * Save Discord message to DB.
     * @param botId target event's bot id.
     * @param discordMessage Discord message to save.
     * @param isFromSelf is from self flag.
     */
    @Transactional
    public void saveMessage(Long botId, Message discordMessage, boolean isFromSelf) {
        try {
            boolean exists = messageRepository.existsByBotIdAndDiscordMessageId(
                    botId, discordMessage.getId()
            );

            if (exists) {
                log.debug("Message {} already saved", discordMessage.getId());
                return;
            }

            DiscordPrivateMessage message = DiscordPrivateMessage.builder()
                    .botId(botId)
                    .discordMessageId(discordMessage.getId())
                    .channelId(discordMessage.getChannel().getId())
                    .authorId(discordMessage.getAuthor().getId())
                    .authorName(discordMessage.getAuthor().getName())
                    .content(discordMessage.getContentRaw())
                    .isFromBot(isFromSelf)
                    .timestamp(discordMessage.getTimeCreated().toLocalDateTime())
                    .createdDate(LocalDateTime.now())
                    .hasAttachments(!discordMessage.getAttachments().isEmpty())
                    .build();

            DiscordPrivateMessage savedMessage = messageRepository.save(message);

            if (!discordMessage.getAttachments().isEmpty()) {
                saveMessageFiles(savedMessage, discordMessage.getAttachments());
            }



            log.debug("Saved DM from {}: {}",
                    discordMessage.getAuthor().getName(),
                    discordMessage.getContentDisplay().length() > 50 ?
                            discordMessage.getContentDisplay().substring(0, 50) + "..." :
                            discordMessage.getContentDisplay()
            );

        } catch (Exception e) {
            log.error("Error saving DM: {}", e.getMessage(), e);
        }
    }

    /**
     * Save Discord message attachments files to DB
     * @param message Discord message with attachments.
     * @param attachments Discord attachments to save.
     */
    private void saveMessageFiles(DiscordPrivateMessage message, List<Message.Attachment> attachments) {
        for (Message.Attachment attachment : attachments) {
            try {
                DiscordMessageFile file = DiscordMessageFile.builder()
                        .discordMessage(message)
                        .fileName(attachment.getFileName())
                        .fileType(attachment.getContentType())
                        .discordUrl(attachment.getUrl())
                        .createdDate(LocalDateTime.now())
                        .build();

                fileRepository.save(file);

            } catch (Exception e) {
                log.error("Error saving file {}: {}",
                        attachment.getFileName(), e.getMessage());
            }
        }
    }

    /**
     * Create or update Discord chat with new message.
     * @param botId target event's bot id.
     * @param message Discord message.
     */
    private void createOrUpdateChat(Long botId, Message message) {
        User author = message.getAuthor();
        PrivateChannel channel = message.getChannel().asPrivateChannel();
        User otherUser = channel.getUser();

        final String botUserId = credentialsRepository.findById(botId)
                .map(DiscordCredentials::getBotUserId)
                .orElseThrow(() -> new RuntimeException("Bot not found"));
        boolean isFromOurBot = author.getId().equals(botUserId);
        User chatUser = isFromOurBot ? otherUser : author;

        DiscordPrivateChat chat = chatRepository.findByBotIdAndUserId(botId, chatUser.getId())
                .orElseGet(() -> DiscordPrivateChat.builder()
                        .botId(botId)
                        .channelId(channel.getId())
                        .userId(chatUser.getId())
                        .userName(chatUser.getName())
                        .userAvatarUrl(chatUser.getEffectiveAvatarUrl())
                        .lastMessageTime(message.getTimeCreated().toLocalDateTime())
                        .lastMessageId(message.getId())
                        .messageCount(1)
                        .build()
                );

        if (chat.getId() == null) {
            chatRepository.save(chat);
        } else {
            chatRepository.updateChatStats(
                    botId,
                    channel.getId(),
                    message.getTimeCreated().toLocalDateTime(),
                    message.getId()
            );
        }
    }
}
