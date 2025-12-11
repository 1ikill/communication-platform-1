package com.sdc.discord.service;

import com.sdc.discord.config.DiscordCredentialsManager;
import com.sdc.discord.domain.dto.chat.DiscordPrivateChatDto;
import com.sdc.discord.domain.mapper.DiscordPrivateChatMapper;
import com.sdc.discord.domain.model.DiscordCredentials;
import com.sdc.discord.domain.model.DiscordPrivateChat;
import com.sdc.discord.repository.DiscordCredentialsRepository;
import com.sdc.discord.repository.DiscordPrivateChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

/**
 * Service for {@link DiscordPrivateChat}
 * @since 12.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordPrivateChatService {
    private final DiscordPrivateChatRepository chatRepository;
    private final DiscordCredentialsRepository credentialsRepository;
    private final DiscordCredentialsManager credentialsManager;
    private final DiscordPrivateChatMapper privateChatMapper;

    private DiscordPrivateChat updateUserInfoIfChanged(DiscordPrivateChat chat, JDA jda) {
        try {
            User freshUser = jda.retrieveUserById(chat.getUserId()).complete();

            if (nonNull(freshUser)) {
                boolean updated = false;

                if (!freshUser.getName().equals(chat.getUserName())) {
                    chat.setUserName(freshUser.getName());
                    updated = true;
                }

                String freshAvatar = freshUser.getEffectiveAvatarUrl();
                if (!Objects.equals(freshAvatar, chat.getUserAvatarUrl())) {
                    chat.setUserAvatarUrl(freshAvatar);
                    updated = true;
                }

                if (updated) {
                    chatRepository.save(chat);
                }
            }

        } catch (Exception e) {
            log.debug("Could not fetch fresh data for user {}: {}",
                    chat.getUserId(), e.getMessage());
        }

        return chat;
    }

    public List<DiscordPrivateChatDto> getChats(Long botId) throws Exception {
        DiscordCredentials bot = credentialsRepository.findById(botId).orElseThrow();
        JDA jda = credentialsManager.getJda(bot);

        final List<DiscordPrivateChat> chats = chatRepository.findByBotIdOrderByLastMessageTimeDesc(botId);

        final List<DiscordPrivateChat> updatedChats = new ArrayList<>();
        chats.forEach(chat -> updatedChats.add(updateUserInfoIfChanged(chat, jda)));

        chatRepository.saveAll(updatedChats);

        return updatedChats.stream()
                .map(privateChatMapper::toDto)
                .toList();
    }

    public List<DiscordPrivateChatDto> searchChats(Long botId, String query) throws Exception {
        DiscordCredentials bot = credentialsRepository.findById(botId).orElseThrow();
        JDA jda = credentialsManager.getJda(bot);

        final List<DiscordPrivateChat> chats = chatRepository.searchByUserName(botId, query);

        final List<DiscordPrivateChat> updatedChats = new ArrayList<>();
        chats.forEach(chat -> updatedChats.add(updateUserInfoIfChanged(chat, jda)));

        chatRepository.saveAll(updatedChats);

        return updatedChats.stream()
                .map(privateChatMapper::toDto)
                .toList();
    }

    public DiscordPrivateChatDto getChat(Long botId, String channelId) throws Exception {
        DiscordCredentials bot = credentialsRepository.findById(botId).orElseThrow();
        JDA jda = credentialsManager.getJda(bot);

        return privateChatMapper.toDto(updateUserInfoIfChanged(chatRepository.findByBotIdAndChannelId(botId, channelId).orElseThrow(), jda));
    }
}
