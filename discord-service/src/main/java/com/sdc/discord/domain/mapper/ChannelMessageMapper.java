package com.sdc.discord.domain.mapper;

import com.sdc.discord.domain.dto.message.ChannelMessageDto;
import net.dv8tion.jda.api.entities.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * Mapper for channel message.
 * @since 12.2025
 */
@Mapper(uses = DiscordMessageFileMapper.class)
public abstract class ChannelMessageMapper {
    @Mapping(target = "id", source = "source.id")
    @Mapping(target = "authorName", source = "source.author.name")
    @Mapping(target = "authorId", source = "source.author.id")
    @Mapping(target = "content", source = "source.contentRaw")
    @Mapping(target = "timestamp", source = "creationTime")
    @Mapping(target = "bot", source = "source.author.bot")
    @Mapping(target = "self", source = "isSelf")
    @Mapping(target = "attachments", source = "attachments")
    public abstract ChannelMessageDto toDto(final Message source, final OffsetDateTime creationTime, final Boolean isSelf, final List<Message.Attachment> attachments);

    protected LocalDateTime mapOffsetDateTime(final OffsetDateTime time) {
        if (nonNull(time)) {
            return time.toLocalDateTime();
        }
        return null;
    }
}
