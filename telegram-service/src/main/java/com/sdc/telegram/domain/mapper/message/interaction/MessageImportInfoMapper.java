package com.sdc.telegram.domain.mapper.message.interaction;

import com.sdc.telegram.domain.dto.tdlib.message.interaction.MessageImportInfoDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper
public abstract class MessageImportInfoMapper {
    public abstract MessageImportInfoDto toDto(final TdApi.MessageImportInfo source);
}
