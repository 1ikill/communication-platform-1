package com.sdc.telegram.domain.mapper.message.interaction;

import com.sdc.telegram.domain.dto.tdlib.message.interaction.ForwardSourceDto;
import com.sdc.telegram.domain.mapper.message.sending.MessageSenderTdlibMapper;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper(uses = MessageSenderTdlibMapper.class)
public abstract class ForwardSourceMapper {
    public abstract ForwardSourceDto toDto(final TdApi.ForwardSource source);
}
