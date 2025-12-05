package com.sdc.telegram.domain.mapper.message.sending;

import com.sdc.telegram.domain.dto.tdlib.message.sending.MessageSenderChatDto;
import com.sdc.telegram.domain.dto.tdlib.message.sending.MessageSenderTdlib;
import com.sdc.telegram.domain.dto.tdlib.message.sending.MessageSenderUserDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper
public abstract class MessageSenderTdlibMapper {
    public MessageSenderTdlib toDto(final TdApi.MessageSender source){
        if (source instanceof TdApi.MessageSenderUser){
            return toMessageSenderUserDto((TdApi.MessageSenderUser) source);
        } else if (source instanceof TdApi.MessageSenderChat) {
            return toMessageSenderChatDto((TdApi.MessageSenderChat) source);
        }
        return null;
    }

    public abstract MessageSenderUserDto toMessageSenderUserDto(final TdApi.MessageSenderUser source);
    
    public abstract MessageSenderChatDto toMessageSenderChatDto(final TdApi.MessageSenderChat source);
}
