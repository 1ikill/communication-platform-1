package com.sdc.discord.domain.mapper;

import com.sdc.discord.domain.dto.bot.DiscordBotInfoDto;
import com.sdc.discord.domain.model.DiscordCredentials;
import org.mapstruct.Mapper;

/**
 * Mapper for {@link DiscordCredentials}
 * @since 12.2025
 */
@Mapper
public abstract class DiscordCredentialsMapper {
    public abstract DiscordBotInfoDto toDto(final DiscordCredentials source);
}
