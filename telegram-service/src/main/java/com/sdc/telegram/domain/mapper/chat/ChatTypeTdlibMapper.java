package com.sdc.telegram.domain.mapper.chat;

import com.sdc.telegram.domain.dto.tdlib.chat.type.ChatTypeBasicGroupDto;
import com.sdc.telegram.domain.dto.tdlib.chat.type.ChatTypePrivateDto;
import com.sdc.telegram.domain.dto.tdlib.chat.type.ChatTypeSupergroupDto;
import com.sdc.telegram.domain.dto.tdlib.chat.type.ChatTypeTdlib;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

/**
 *
 */
@Mapper
public abstract class ChatTypeTdlibMapper {
    public ChatTypeTdlib toDto(final TdApi.ChatType source){
        if (source instanceof TdApi.ChatTypePrivate){
            return toChatTypePrivateDto((TdApi.ChatTypePrivate) source);
        } else if (source instanceof  TdApi.ChatTypeBasicGroup) {
            return toChatTypeBasicGroupDto((TdApi.ChatTypeBasicGroup) source);
        } else if (source instanceof TdApi.ChatTypeSupergroup) {
            return toChatTypeSupergroupDto((TdApi.ChatTypeSupergroup) source);
        }
        return null;
    }

    public abstract ChatTypePrivateDto toChatTypePrivateDto(final TdApi.ChatTypePrivate source);

    public abstract ChatTypeBasicGroupDto toChatTypeBasicGroupDto(final TdApi.ChatTypeBasicGroup source);

    public abstract ChatTypeSupergroupDto toChatTypeSupergroupDto(final TdApi.ChatTypeSupergroup source);
}
