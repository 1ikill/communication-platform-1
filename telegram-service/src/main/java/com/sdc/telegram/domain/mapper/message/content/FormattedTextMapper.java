package com.sdc.telegram.domain.mapper.message.content;

import com.sdc.telegram.domain.dto.tdlib.message.content.FormattedTextDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper
public abstract class FormattedTextMapper {
    public abstract FormattedTextDto toDto(final TdApi.FormattedText source);
}
