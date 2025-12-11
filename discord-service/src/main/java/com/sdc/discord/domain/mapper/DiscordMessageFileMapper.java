package com.sdc.discord.domain.mapper;

import com.sdc.discord.domain.dto.message.DiscordMessageFileDto;
import com.sdc.discord.domain.model.DiscordMessageFile;
import net.dv8tion.jda.api.entities.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper for {@link DiscordMessageFile}.
 * @since 12.2025
 */
@Mapper
public abstract class DiscordMessageFileMapper {
    public abstract DiscordMessageFileDto fromAttachment(final DiscordMessageFile source);

    @Mapping(target = "fileType", source = "contentType")
    @Mapping(target = "discordUrl", source = "url")
    public abstract DiscordMessageFileDto fromAttachment(final Message.Attachment source);

    public abstract List<DiscordMessageFileDto> fromEntity(final List<DiscordMessageFile> source);

    public abstract List<DiscordMessageFileDto> fromAttachment(final List<Message.Attachment> source);
}
