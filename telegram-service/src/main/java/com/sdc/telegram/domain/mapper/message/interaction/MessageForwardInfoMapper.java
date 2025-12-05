package com.sdc.telegram.domain.mapper.message.interaction;

import com.sdc.telegram.domain.dto.tdlib.message.interaction.MessageForwardInfoDto;
import com.sdc.telegram.domain.mapper.message.origin.MessageOriginTdlibMapper;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper(uses = {MessageOriginTdlibMapper.class, ForwardSourceMapper.class})
public abstract class MessageForwardInfoMapper {
    public abstract MessageForwardInfoDto toDto(final TdApi.MessageForwardInfo source);
}
