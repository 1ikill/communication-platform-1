package com.sdc.telegram.domain.mapper.message.content;

import com.sdc.telegram.domain.dto.tdlib.message.content.PhotoSizeDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper(uses = FileMapper.class)
public abstract class PhotoSizeMapper {
    public abstract PhotoSizeDto toDto(final TdApi.PhotoSize source);
}
