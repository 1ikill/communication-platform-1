package com.sdc.telegram.domain.mapper.message.sending;

import com.sdc.telegram.domain.dto.tdlib.message.sending.MessageSendingStateFailedDto;
import com.sdc.telegram.domain.dto.tdlib.message.sending.MessageSendingStatePendingDto;
import com.sdc.telegram.domain.dto.tdlib.message.sending.MessageSendingStateTdlib;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper
public abstract class MessageSendingStateTdlibMapper {
    public MessageSendingStateTdlib toDto(final TdApi.MessageSendingState source){
        if (source instanceof TdApi.MessageSendingStateFailed){
            return toMessageSendingStateFailedDto((TdApi.MessageSendingStateFailed) source);
        } else if (source instanceof TdApi.MessageSendingStatePending) {
            return toMessageSendingStatePendingDto((TdApi.MessageSendingStatePending) source);
        }
        return null;
    }

    public abstract MessageSendingStateFailedDto toMessageSendingStateFailedDto(final TdApi.MessageSendingStateFailed source);
    
    public abstract MessageSendingStatePendingDto toMessageSendingStatePendingDto(final TdApi.MessageSendingStatePending source);
}
