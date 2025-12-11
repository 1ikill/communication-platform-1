package com.sdc.discord.domain.mapper;

import com.sdc.discord.domain.dto.message.DiscordPrivateMessageDto;
import com.sdc.discord.domain.model.DiscordPrivateMessage;
import org.mapstruct.Mapper;

/**
 * Mapper for {@link DiscordPrivateMessage}
 * @since 12.2025
 */
@Mapper(uses = DiscordMessageFileMapper.class)
public abstract class DiscordPrivateMessageMapper {
    public abstract DiscordPrivateMessageDto toDto(final DiscordPrivateMessage source);
}
