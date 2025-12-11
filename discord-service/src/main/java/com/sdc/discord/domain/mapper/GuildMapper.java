package com.sdc.discord.domain.mapper;

import com.sdc.discord.domain.dto.guild.GuildDto;
import net.dv8tion.jda.api.entities.Guild;
import org.mapstruct.Mapper;

/**
 * Mapper for guild.
 * @since 12.2025
 */
@Mapper
public abstract class GuildMapper {
    public abstract GuildDto toDto(final Guild source);
}
