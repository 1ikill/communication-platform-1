package com.sdc.discord.domain.mapper;

import com.sdc.discord.domain.dto.guild.GuildUserDto;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for guild user.
 * @since 12.2025
 */
@Mapper
public abstract class GuildUserMapper {
    @Mapping(target = "id", source = "source.id")
    @Mapping(target = "username", source = "source.name")
    @Mapping(target = "isBot", source = "source.bot")
    @Mapping(target = "guildName", source = "guild.name")
    public abstract GuildUserDto toDto(final User source, final Guild guild);
}
