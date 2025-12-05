package com.sdc.telegram.domain.mapper.message.content;

import com.sdc.telegram.domain.dto.tdlib.message.content.MinithumbnailDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper
public abstract class MinithumbnailMapper {
    public abstract MinithumbnailDto toDto(final TdApi.Minithumbnail source);
}
