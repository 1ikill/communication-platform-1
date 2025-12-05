package com.sdc.telegram.domain.mapper.message.origin;

import com.sdc.telegram.domain.dto.tdlib.message.origin.MessageOriginChanelDto;
import com.sdc.telegram.domain.dto.tdlib.message.origin.MessageOriginChatDto;
import com.sdc.telegram.domain.dto.tdlib.message.origin.MessageOriginHiddenUserDto;
import com.sdc.telegram.domain.dto.tdlib.message.origin.MessageOriginTdlib;
import com.sdc.telegram.domain.dto.tdlib.message.origin.MessageOriginUserDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper
public abstract class MessageOriginTdlibMapper {
    public MessageOriginTdlib toDto(final TdApi.MessageOrigin source){
        if (source instanceof TdApi.MessageOriginHiddenUser) {
            return toMessageOriginHiddenUserDto((TdApi.MessageOriginHiddenUser) source);
        } else if (source instanceof TdApi.MessageOriginChat) {
            return toMessageOriginChatDto((TdApi.MessageOriginChat) source);
        } else if (source instanceof TdApi.MessageOriginChannel) {
            return toMessageOriginChanelDto((TdApi.MessageOriginChannel) source);
        } else if (source instanceof TdApi.MessageOriginUser){
            return toMessageOriginUserDto((TdApi.MessageOriginUser) source);
        }
        return null;
    }

    public abstract MessageOriginChanelDto toMessageOriginChanelDto(final TdApi.MessageOriginChannel source);
    
    public abstract MessageOriginChatDto toMessageOriginChatDto(final TdApi.MessageOriginChat source);
    
    public abstract MessageOriginUserDto toMessageOriginUserDto(final TdApi.MessageOriginUser source);
    
    public abstract MessageOriginHiddenUserDto toMessageOriginHiddenUserDto(final TdApi.MessageOriginHiddenUser source);
}
