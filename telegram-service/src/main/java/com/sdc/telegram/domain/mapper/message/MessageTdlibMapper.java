package com.sdc.telegram.domain.mapper.message;

import com.sdc.telegram.domain.dto.tdlib.message.MessageTdlibDto;
import com.sdc.telegram.domain.mapper.message.content.MessageContentTdlibMapper;
import com.sdc.telegram.domain.mapper.message.interaction.MessageForwardInfoMapper;
import com.sdc.telegram.domain.mapper.message.interaction.MessageImportInfoMapper;
import com.sdc.telegram.domain.mapper.message.interaction.MessageInteractionInfoMapper;
import com.sdc.telegram.domain.mapper.message.interaction.MessageReplyToTdlibMapper;
import com.sdc.telegram.domain.mapper.message.sending.MessageSenderTdlibMapper;
import com.sdc.telegram.domain.mapper.message.sending.MessageSendingStateTdlibMapper;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {MessageContentTdlibMapper.class, MessageSendingStateTdlibMapper.class, MessageSenderTdlibMapper.class,
        MessageReplyToTdlibMapper.class, MessageForwardInfoMapper.class, MessageImportInfoMapper.class,
        MessageInteractionInfoMapper.class})
public abstract class MessageTdlibMapper {

    @Mapping(target = "photoRemoteId", ignore = true)
    @Mapping(target = "videoRemoteId", ignore = true)
    @Mapping(target = "isRead", ignore = true)
    @Mapping(target = "documentRemoteId", ignore = true)
    public abstract MessageTdlibDto toDto(final TdApi.Message source);

    public abstract List<MessageTdlibDto> toDto(final List<TdApi.Message> list);
}
