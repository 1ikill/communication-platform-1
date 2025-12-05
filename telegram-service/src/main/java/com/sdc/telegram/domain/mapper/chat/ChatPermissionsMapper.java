package com.sdc.telegram.domain.mapper.chat;

import com.sdc.telegram.domain.dto.tdlib.chat.ChatPermissionsDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper
public abstract class ChatPermissionsMapper {
    public abstract ChatPermissionsDto toDto(final TdApi.ChatPermissions source);
}
