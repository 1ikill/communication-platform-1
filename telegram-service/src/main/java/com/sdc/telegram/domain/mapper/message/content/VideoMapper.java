package com.sdc.telegram.domain.mapper.message.content;

import com.sdc.telegram.domain.dto.tdlib.message.content.VideoDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;


@Mapper(uses = {MinithumbnailMapper.class, FileMapper.class})
public abstract class VideoMapper {

    public abstract VideoDto toVideoDto(final TdApi.Video source);
}
