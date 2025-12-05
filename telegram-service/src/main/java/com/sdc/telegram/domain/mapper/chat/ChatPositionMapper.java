package com.sdc.telegram.domain.mapper.chat;

import com.sdc.telegram.domain.dto.tdlib.chat.ChatPositionDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper(uses = ChatListTdlibMapper.class)
public abstract class ChatPositionMapper {
    public abstract ChatPositionDto toDto(final TdApi.ChatPosition source);
}
