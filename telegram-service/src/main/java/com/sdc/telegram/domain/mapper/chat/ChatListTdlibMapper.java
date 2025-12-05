package com.sdc.telegram.domain.mapper.chat;

import com.sdc.telegram.domain.dto.tdlib.chat.list.ChatListArchiveDto;
import com.sdc.telegram.domain.dto.tdlib.chat.list.ChatListFolderDto;
import com.sdc.telegram.domain.dto.tdlib.chat.list.ChatListMainDto;
import com.sdc.telegram.domain.dto.tdlib.chat.list.ChatListTdlib;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper
public abstract class ChatListTdlibMapper {

    public ChatListTdlib toDto(final TdApi.ChatList source){
        if (source instanceof TdApi.ChatListMain){
            return toChatListMainDto((TdApi.ChatListMain) source);
        } else if (source instanceof TdApi.ChatListArchive) {
            return toChatListArchiveDto((TdApi.ChatListArchive) source);
        } else if (source instanceof TdApi.ChatListFolder) {
            return toChatListFolderDto((TdApi.ChatListFolder) source);
        }
        return null;
    }

    public abstract ChatListFolderDto toChatListFolderDto(final TdApi.ChatListFolder source);

    public abstract ChatListMainDto toChatListMainDto(final TdApi.ChatListMain source);
    
    public abstract ChatListArchiveDto toChatListArchiveDto(final TdApi.ChatListArchive source);
    
}
