package com.sdc.discord.service;

import com.sdc.discord.config.DiscordCredentialsManager;
import com.sdc.discord.config.security.CurrentUser;
import com.sdc.discord.domain.dto.chat.DiscordPrivateChatDto;
import com.sdc.discord.domain.dto.guild.GuildChannelDto;
import com.sdc.discord.domain.dto.guild.GuildDto;
import com.sdc.discord.domain.dto.guild.GuildUserDto;
import com.sdc.discord.domain.exception.BadRequestException;
import com.sdc.discord.domain.exception.NotFoundException;
import com.sdc.discord.domain.mapper.DiscordPrivateChatMapper;
import com.sdc.discord.domain.mapper.GuildChannelMapper;
import com.sdc.discord.domain.mapper.GuildMapper;
import com.sdc.discord.domain.mapper.GuildUserMapper;
import com.sdc.discord.domain.model.DiscordCredentials;
import com.sdc.discord.domain.model.DiscordPrivateChat;
import com.sdc.discord.repository.DiscordCredentialsRepository;
import com.sdc.discord.repository.DiscordPrivateChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.nonNull;

/**
 * Service for {@link DiscordPrivateChat}
 * @since 12.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordChatService {
    private final DiscordPrivateChatRepository chatRepository;
    private final DiscordCredentialsRepository credentialsRepository;
    private final DiscordCredentialsManager credentialsManager;
    private final DiscordPrivateChatMapper privateChatMapper;
    private final GuildMapper guildMapper;
    private final GuildChannelMapper guildChannelMapper;
    private final GuildUserMapper guildUserMapper;
    private final CurrentUser currentUser;

    /**
     * Get available users list.
     * @param botId current bot id.
     * @return GuildUserDto list of available users
     */
    public List<GuildUserDto> getAvailableUsers(final Long botId) {
        final JDA jda = getJda(botId);

        for (Guild guild : jda.getGuilds()) {
            guild.loadMembers().onError(Throwable::printStackTrace).get();
        }

        final Set<String> seenUserIds = new HashSet<>();
        final List<GuildUserDto> users = new ArrayList<>();

        for (Guild guild : jda.getGuilds()) {
            for (Member member : guild.getMembers()) {
                final User user = member.getUser();
                if (user.isBot() || Objects.equals(user.getId(), jda.getSelfUser().getId()) || seenUserIds.contains(user.getId()) ) {
                    continue;
                }
                seenUserIds.add(user.getId());

                users.add(guildUserMapper.toDto(user, guild));
            }
        }

        return users.stream()
                .sorted(Comparator.comparing(GuildUserDto::getUsername))
                .toList();
    }

    /**
     * Get guild text channels.
     * @param botId current bot id.
     * @param guildId guild id.
     * @return GuildChannelDto list.
     */
    public List<GuildChannelDto> getGuildChannels(final Long botId, final String guildId) {
        final JDA jda = getJda(botId);

        final Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            throw new NotFoundException("Server not found");
        }

        return guild.getTextChannels().stream()
                .map(guildChannelMapper::toDto)
                .toList();
    }

    /**
     * Get bot's guilds.
     * @param botId current bot.
     * @return GuildDto list.
     */
    public List<GuildDto> getGuilds(final Long botId) {
        final JDA jda = getJda(botId);

        return jda.getGuilds().stream()
                .map(guildMapper::toDto)
                .toList();
    }

    /**
     * Get private chats list.
     * @param botId current bot id.
     * @return DiscordPrivateChatDto list.
     */
    public List<DiscordPrivateChatDto> getChats(final Long botId) {
        final JDA jda = getJda(botId);

        final List<DiscordPrivateChat> chats = chatRepository.findByBotIdOrderByLastMessageTimeDesc(botId);

        final List<DiscordPrivateChat> updatedChats = new ArrayList<>();
        chats.forEach(chat -> updatedChats.add(updateUserInfoIfChanged(chat, jda)));

        chatRepository.saveAll(updatedChats);

        return updatedChats.stream()
                .map(privateChatMapper::toDto)
                .toList();
    }

    /**
     * Search private chats.
     * @param botId current bot id.
     * @param query search query.
     * @return DiscordPrivateChatDto list with matched results.
     */
    public List<DiscordPrivateChatDto> searchChats(final Long botId, final String query) {
        final JDA jda = getJda(botId);

        final List<DiscordPrivateChat> chats = chatRepository.searchByUserName(botId, query);

        final List<DiscordPrivateChat> updatedChats = new ArrayList<>();
        chats.forEach(chat -> updatedChats.add(updateUserInfoIfChanged(chat, jda)));

        chatRepository.saveAll(updatedChats);

        return updatedChats.stream()
                .map(privateChatMapper::toDto)
                .toList();
    }

    /**
     * Search guild channels.
     * @param botId current bot id.
     * @param guildId guild id.
     * @param query search query.
     * @return GuildChannelDto list with matched results.
     */
    public List<GuildChannelDto> searchGuildChannels(final Long botId, final String guildId, final String query) {
        final List<GuildChannelDto> channels = getGuildChannels(botId, guildId);
        return channels.stream()
                .filter(obj -> obj.getName() != null &&
                        obj.getName().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }

    /**
     * Get private chat.
     * @param botId current bot id.
     * @param channelId chat channel id.
     * @return DiscordPrivateChatDto.
     */
    public DiscordPrivateChatDto getChat(final Long botId, final String channelId) {
        final JDA jda = getJda(botId);

        return privateChatMapper.toDto(updateUserInfoIfChanged(chatRepository.findByBotIdAndChannelId(botId, channelId).orElseThrow(), jda));
    }

    /**
     * Helper method to update user info from Discord API.
     * @param chat chat to update.
     * @param jda Discord JDA.
     * @return DiscordPrivateChat updated chat.
     */
    private DiscordPrivateChat updateUserInfoIfChanged(final DiscordPrivateChat chat, final JDA jda) {
        final User freshUser = jda.retrieveUserById(chat.getUserId()).complete();

        if (nonNull(freshUser)) {
            boolean updated = false;

            if (!freshUser.getName().equals(chat.getUserName())) {
                chat.setUserName(freshUser.getName());
                updated = true;
            }

            final String freshAvatar = freshUser.getEffectiveAvatarUrl();
            if (!Objects.equals(freshAvatar, chat.getUserAvatarUrl())) {
                chat.setUserAvatarUrl(freshAvatar);
                updated = true;
            }

            if (updated) {
                chatRepository.save(chat);
            }
        }

        return chat;
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
