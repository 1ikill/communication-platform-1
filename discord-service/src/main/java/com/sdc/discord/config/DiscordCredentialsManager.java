package com.sdc.discord.config;

import com.sdc.discord.domain.model.DiscordCredentials;
import com.sdc.discord.listener.DiscordMessageListener;
import com.sdc.discord.repository.DiscordCredentialsRepository;
import com.sdc.discord.utils.CryptoUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Discord api-credentials manager.
 * @since 12.2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordCredentialsManager {
    private final static String ACTIVITY_NAME = "Communication Platform";
    private final DiscordCredentialsRepository credentialsRepository;
    private final CryptoUtils cryptoUtils;
    private final DiscordMessageListener messageListener;

    private final Map<String, JDA> activeBots = new ConcurrentHashMap<>();

    /**
     * All bots initialization on app start.
     */
    @PostConstruct
    public void initAllBots() {
        log.info("Starting Discord bots initialization...");

        final List<DiscordCredentials> activeBots = credentialsRepository.findAllByIsActive(true);

        if (activeBots.isEmpty()) {
            log.info("No active Discord bots found");
            return;
        }

        log.info("Found {} active Discord bots to initialize", activeBots.size());
        
        activeBots.forEach(bot -> {
            try {
                initBot(bot);
            } catch (Exception e) {
                log.error("Failed to initialize bot {}: {}", bot.getBotUsername(), e.getMessage());
            }
        });
    }

    /**
     * Bot initialization.
     * @param bot DiscordCredentials with bot credentials.
     */
    public void initBot(final DiscordCredentials bot)  {
        final String token = cryptoUtils.decrypt(bot.getBotToken());

        final JDA jda = JDABuilder.createLight(token)
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
                .setEnabledIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.DIRECT_MESSAGES
                )
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .addEventListeners(messageListener)
                .setActivity(Activity.watching(ACTIVITY_NAME))
                .setStatus(OnlineStatus.ONLINE)
                .build();

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        messageListener.registerBot(bot);
        activeBots.put(bot.getBotUserId(), jda);
        log.debug("Bot '{}' initialized successfully. Guilds: {}",
                bot.getBotUsername(), jda.getGuilds().size());
    }

    /**
     * Get bot's JDA (supports lazy initialization).
     * @param bot bot credentials.
     * @return Discord JDA.
     */
    public JDA getJda(final DiscordCredentials bot) {
        JDA jda = activeBots.get(bot.getBotUserId());

        if (jda == null || jda.getStatus() != JDA.Status.CONNECTED) {
            log.debug("Bot {} is not connected, reinitializing...", bot.getBotUsername());
            if (jda != null) {
                jda.shutdown();
            }
            initBot(bot);
            jda = activeBots.get(bot.getBotUserId());
        }

        return jda;
    }

    /**
     * Stop all bots at app shutdown.
     */
    @PreDestroy
    public void shutdownAllBots() {
        log.info("Shutting down all Discord bots...");

        activeBots.forEach((botUserId, jda) -> {
            try {
                jda.shutdown();
                log.debug("Bot {} shutdown", botUserId);
            } catch (Exception e) {
                log.error("Error shutting down bot {}: {}", botUserId, e.getMessage());
            }
        });
        activeBots.clear();
    }

    /**
     * Bot connection check.
     * @param botUserId bot Discord user id.
     * @return isBotConnected flag.
     */
    public boolean isBotConnected(final String botUserId) {
        final JDA jda = activeBots.get(botUserId);
        return jda != null && jda.getStatus() == JDA.Status.CONNECTED;
    }

    /**
     * Get connected bots stats.
     * @return bot stats.
     */
    public Map<String, Object> getBotStats() {
        final Map<String, Object> stats = new HashMap<>();
        stats.put("totalBots", activeBots.size());

        final List<Map<String, Object>> botDetails = new ArrayList<>();

        activeBots.forEach((botUserId, jda) -> {
            Map<String, Object> detail = new HashMap<>();
            detail.put("botUserId", botUserId);
            detail.put("status", jda.getStatus().name());
            detail.put("guildCount", jda.getGuilds().size());
            detail.put("ping", jda.getGatewayPing());
            botDetails.add(detail);
        });
        stats.put("bots", botDetails);
        return stats;
    }
}
