package com.sdc.telegram.domain.mapper.message.content;

import com.sdc.telegram.domain.dto.tdlib.message.content.AlternativeVideoDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper(uses = FileMapper.class)
public abstract class AlternativeVideoMapper {
    public abstract AlternativeVideoDto toAlternativeVideoDto(final TdApi.AlternativeVideo source);
}
