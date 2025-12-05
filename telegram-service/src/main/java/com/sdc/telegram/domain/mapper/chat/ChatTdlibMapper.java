package com.sdc.telegram.domain.mapper.chat;

import com.sdc.telegram.domain.dto.tdlib.chat.ChatTdlibDto;
import com.sdc.telegram.domain.mapper.message.MessageTdlibMapper;
import com.sdc.telegram.domain.mapper.message.sending.MessageSenderTdlibMapper;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {ChatListTdlibMapper.class, MessageTdlibMapper.class, MessageSenderTdlibMapper.class,
        ChatPositionMapper.class, ChatPermissionsMapper.class, ChatNotificationSettingsMapper.class,
        ChatJoinRequestsInfoMapper.class, ChatTypeTdlibMapper.class})
public abstract class ChatTdlibMapper {

    @Mapping(target = "profilePhotoUrl", ignore = true)
    public abstract ChatTdlibDto toDto(final TdApi.Chat source);


    public abstract List<ChatTdlibDto> toDto(final List<TdApi.Chat> list);
}
