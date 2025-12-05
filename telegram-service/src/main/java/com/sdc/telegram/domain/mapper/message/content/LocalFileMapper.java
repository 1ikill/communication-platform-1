package com.sdc.telegram.domain.mapper.message.content;

import com.sdc.telegram.domain.dto.tdlib.message.content.LocalFileDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper
public abstract class LocalFileMapper {
    public abstract LocalFileDto toDto(final TdApi.LocalFile source);
}
