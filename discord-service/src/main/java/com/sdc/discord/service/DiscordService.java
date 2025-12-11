package com.sdc.discord.service;

import com.sdc.discord.config.DiscordCredentialsManager;
import com.sdc.discord.config.security.CurrentUser;
import com.sdc.discord.domain.dto.bot.DiscordBotInfoDto;
import com.sdc.discord.domain.dto.request.AddBotRequestDto;
import com.sdc.discord.domain.mapper.DiscordCredentialsMapper;
import com.sdc.discord.repository.DiscordCredentialsRepository;
import com.sdc.discord.utils.CryptoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.stereotype.Service;
import com.sdc.discord.domain.model.DiscordCredentials;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for {@link DiscordCredentials}.
 * @since 12.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordService {
    private final DiscordCredentialsRepository credentialsRepository;
    private final CryptoUtils cryptoUtils;
    private final CurrentUser currentUser;
    private final DiscordCredentialsManager credentialsManager;
    private final DiscordCredentialsMapper credentialsMapper;

    /**
     * Get current user's connected bots.
     * @return DiscordBotIfoDto list of info about connected bots.
     */
    public List<DiscordBotInfoDto> getConnectedBots() {
        return credentialsRepository.findAllByUserIdAndIsActive(currentUser.getId(), true).stream()
                .map(credentialsMapper::toDto)
                .toList();
    }

    /**
     * Add new bot credentials.
     * @param requestDto request dto for bot connection.
     * @return DiscordBotInfoDto info.
     */
    public DiscordBotInfoDto addBot(final AddBotRequestDto requestDto) {
        final String botToken = requestDto.getToken();
        JDA testJda;
        try {
            testJda = JDABuilder.createLight(botToken)
                    .disableCache(CacheFlag.ACTIVITY,
                            CacheFlag.VOICE_STATE,
                            CacheFlag.EMOJI,
                            CacheFlag.STICKER,
                            CacheFlag.CLIENT_STATUS,
                            CacheFlag.MEMBER_OVERRIDES,
                            CacheFlag.ROLE_TAGS,
                            CacheFlag.FORUM_TAGS,
                            CacheFlag.ONLINE_STATUS,
                            CacheFlag.SCHEDULED_EVENTS)
                    .setEnabledIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .build()
                    .awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        final SelfUser botUser = testJda.getSelfUser();
        final Long userId = currentUser.getId();
        if (credentialsRepository.existsByUserIdAndBotUserId(userId, botUser.getId())) {
            testJda.shutdown();
            throw new RuntimeException("Bot is already added");
        }

        DiscordCredentials bot = new DiscordCredentials();
        bot.setUserId(userId);
        bot.setBotToken(cryptoUtils.encrypt(botToken));
        bot.setBotUserId(botUser.getId());
        bot.setBotUsername(botUser.getName());
        bot.setIsActive(true);
        bot.setCreatedDate(LocalDateTime.now());

        final DiscordCredentials savedBot = credentialsRepository.save(bot);

        credentialsManager.initBot(savedBot);
        testJda.shutdown();

        return credentialsMapper.toDto(savedBot);
    }

}
