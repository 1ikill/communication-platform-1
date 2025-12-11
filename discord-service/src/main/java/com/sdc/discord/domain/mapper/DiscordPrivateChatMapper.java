package com.sdc.discord.domain.mapper;

import com.sdc.discord.domain.dto.chat.DiscordPrivateChatDto;
import com.sdc.discord.domain.model.DiscordPrivateChat;
import org.mapstruct.Mapper;

/**
 * Mapper for {@link DiscordPrivateChat}.
 * @since 12.2025
 */
@Mapper
public abstract class DiscordPrivateChatMapper {
    public abstract DiscordPrivateChatDto toDto(final DiscordPrivateChat source);
}
