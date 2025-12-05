package com.sdc.telegram.domain.mapper.message.interaction;

import com.sdc.telegram.domain.dto.tdlib.message.interaction.MessageInteractionInfoDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper
public abstract class MessageInteractionInfoMapper {
    public abstract MessageInteractionInfoDto toDto(final TdApi.MessageInteractionInfo source);
}
