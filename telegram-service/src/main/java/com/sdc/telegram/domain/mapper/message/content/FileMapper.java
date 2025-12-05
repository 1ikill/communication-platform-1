package com.sdc.telegram.domain.mapper.message.content;

import com.sdc.telegram.domain.dto.tdlib.message.content.FileDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper(uses = {LocalFileMapper.class, RemoteFileMapper.class})
public abstract class FileMapper {
    public abstract FileDto toDto(final TdApi.File source);
}
