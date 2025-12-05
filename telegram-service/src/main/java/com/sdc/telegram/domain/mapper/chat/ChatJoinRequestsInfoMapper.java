package com.sdc.telegram.domain.mapper.chat;

import com.sdc.telegram.domain.dto.tdlib.chat.ChatJoinRequestsInfoDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper
public abstract class ChatJoinRequestsInfoMapper {
    public abstract ChatJoinRequestsInfoDto toDto(final TdApi.ChatJoinRequestsInfo source);
}
