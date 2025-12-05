package com.sdc.telegram.domain.mapper.chat;

import com.sdc.telegram.domain.dto.tdlib.chat.ChatNotificationSettingsDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper
public abstract class ChatNotificationSettingsMapper {
    public abstract ChatNotificationSettingsDto toDto(final TdApi.ChatNotificationSettings source);
}
