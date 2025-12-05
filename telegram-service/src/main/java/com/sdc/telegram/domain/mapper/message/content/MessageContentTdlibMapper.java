package com.sdc.telegram.domain.mapper.message.content;

import com.sdc.telegram.domain.dto.tdlib.message.content.MessageContentTdlib;
import com.sdc.telegram.domain.dto.tdlib.message.content.MessageDocumentDto;
import com.sdc.telegram.domain.dto.tdlib.message.content.MessagePhotoDto;
import com.sdc.telegram.domain.dto.tdlib.message.content.MessageTextDto;
import com.sdc.telegram.domain.dto.tdlib.message.content.MessageVideoDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static com.sdc.telegram.domain.constants.TelegramContentType.DOCUMENT;
import static com.sdc.telegram.domain.constants.TelegramContentType.IMAGE;
import static com.sdc.telegram.domain.constants.TelegramContentType.TEXT;
import static com.sdc.telegram.domain.constants.TelegramContentType.VIDEO;

@Mapper(uses = {FormattedTextMapper.class, PhotoMapper.class, VideoMapper.class, AlternativeVideoMapper.class})
public abstract class MessageContentTdlibMapper {
    public MessageContentTdlib toDto(final TdApi.MessageContent source) {
        if (source instanceof TdApi.MessagePhoto) {
            MessagePhotoDto dto = toMessagePhotoDto((TdApi.MessagePhoto) source);
            dto.setType(IMAGE);
            return dto;
        } else if (source instanceof TdApi.MessageText) {
            MessageTextDto dto = toMessageTextDto((TdApi.MessageText) source);
            dto.setType(TEXT);
            return dto;
        } else if (source instanceof TdApi.MessageVideo) {
            MessageVideoDto dto = toMessageVideoDto((TdApi.MessageVideo) source);
            dto.setType(VIDEO);
            return dto;
        } else if (source instanceof TdApi.MessageDocument) {
            MessageDocumentDto dto = toMessageDocumentDto((TdApi.MessageDocument) source);
            dto.setType(DOCUMENT);
            return dto;
        }
        return null;
    }

    @Mapping(target = "type", ignore = true)
    public abstract MessagePhotoDto toMessagePhotoDto(final TdApi.MessagePhoto source);

    @Mapping(target = "type", ignore = true)
    public abstract MessageTextDto toMessageTextDto(final TdApi.MessageText source);

    @Mapping(target = "type", ignore = true)
    public abstract MessageVideoDto toMessageVideoDto(final TdApi.MessageVideo source);

    @Mapping(target = "type", ignore = true)
    public abstract MessageDocumentDto toMessageDocumentDto(final TdApi.MessageDocument source);
}
