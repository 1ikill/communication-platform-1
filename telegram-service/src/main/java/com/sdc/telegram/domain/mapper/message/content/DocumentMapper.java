package com.sdc.telegram.domain.mapper.message.content;

import com.sdc.telegram.domain.dto.tdlib.message.content.DocumentDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper(uses = {FileMapper.class, MinithumbnailMapper.class})
public abstract class DocumentMapper {
    public abstract DocumentDto toDto(final TdApi.Document source);
}
