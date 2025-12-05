package com.sdc.telegram.domain.mapper.message.content;

import com.sdc.telegram.domain.dto.tdlib.message.content.TextQuoteDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper(uses = {FormattedTextMapper.class})
public abstract class TextQuoteMapper {
    public abstract TextQuoteDto toDto(final TdApi.TextQuote source);
}
