package com.sdc.telegram.domain.mapper.message.content;

import com.sdc.telegram.domain.dto.tdlib.message.content.PhotoDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper(uses = {MinithumbnailMapper.class, PhotoSizeMapper.class})
public abstract class PhotoMapper {
    public abstract PhotoDto toDto(final TdApi.Photo source);
}
