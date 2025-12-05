package com.sdc.telegram.domain.mapper.message.content;

import com.sdc.telegram.domain.dto.tdlib.message.content.RemoteFileDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper
public abstract class RemoteFileMapper {
    public abstract RemoteFileDto toDto(final TdApi.RemoteFile source);
}
