package com.sdc.main.domain.mapper;

import com.sdc.main.domain.dto.request.GmailMessageRequestDto;
import com.sdc.main.domain.dto.request.MessageRequestDto;
import com.sdc.main.domain.dto.request.TelegramMessageRequestDto;
import com.sdc.main.domain.dto.request.ViberMessageRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for {@link MessageRequestDto}.
 * @since 11.2025
 */
@Mapper
public abstract class MessageRequestMapper {
    public MessageRequestDto fromDto(final MessageRequestDto source, final String message) {
        if (source instanceof TelegramMessageRequestDto) {
            return toTelegramMessageRequestDto((TelegramMessageRequestDto) source, message);
        } else if (source instanceof ViberMessageRequestDto) {
            return toViberMessageRequestDto((ViberMessageRequestDto) source, message);
        } else if (source instanceof GmailMessageRequestDto) {
            return toGmailMessageRequestDto((GmailMessageRequestDto) source, message);
        }

        return null;
    }

    @Mapping(target = "platform", source = "source.platform")
    @Mapping(target = "chatIdentifier", source = "source.chatIdentifier")
    @Mapping(target = "accountId", source = "source.accountId")
    @Mapping(target = "message", source = "message")
    public abstract TelegramMessageRequestDto toTelegramMessageRequestDto(final TelegramMessageRequestDto source, final String message);

    @Mapping(target = "platform", source = "source.platform")
    @Mapping(target = "chatIdentifier", source = "source.chatIdentifier")
    @Mapping(target = "accountId", source = "source.accountId")
    @Mapping(target = "subject", source = "source.subject")
    @Mapping(target = "message", source = "message")
    public abstract GmailMessageRequestDto toGmailMessageRequestDto(final GmailMessageRequestDto source, final String message);

    @Mapping(target = "platform", source = "source.platform")
    @Mapping(target = "chatIdentifier", source = "source.chatIdentifier")
    @Mapping(target = "viberField", source = "source.viberField")
    @Mapping(target = "message", source = "message")
    public abstract ViberMessageRequestDto toViberMessageRequestDto(final ViberMessageRequestDto source, final String message);
}
