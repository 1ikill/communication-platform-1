package com.sdc.discord.domain.mapper;

import com.sdc.discord.domain.dto.guild.GuildChannelDto;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.mapstruct.Mapper;

/**
 * Mapper for guild channel.
 * @since 12.2025
 */
@Mapper
public abstract class GuildChannelMapper {
    public abstract GuildChannelDto toDto(final TextChannel channel);
}
