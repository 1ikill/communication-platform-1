package com.sdc.telegram.domain.mapper.message.interaction;

import com.sdc.telegram.domain.dto.tdlib.message.interaction.MessageReplyToMessageDto;
import com.sdc.telegram.domain.dto.tdlib.message.interaction.MessageReplyToTdlib;
import com.sdc.telegram.domain.mapper.message.content.MessageContentTdlibMapper;
import com.sdc.telegram.domain.mapper.message.content.TextQuoteMapper;
import com.sdc.telegram.domain.mapper.message.origin.MessageOriginTdlibMapper;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper(uses = {MessageOriginTdlibMapper.class, MessageContentTdlibMapper.class, TextQuoteMapper.class})
public abstract class MessageReplyToTdlibMapper {
    public MessageReplyToTdlib toDto(final TdApi.MessageReplyTo source) {
        if (source instanceof TdApi.MessageReplyToMessage){
            return toMessageReplyToMessageDto((TdApi.MessageReplyToMessage) source);
        }
        return null;
    }

    public abstract MessageReplyToMessageDto toMessageReplyToMessageDto(final TdApi.MessageReplyToMessage source);
}
