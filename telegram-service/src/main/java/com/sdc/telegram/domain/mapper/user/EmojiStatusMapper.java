package com.sdc.telegram.domain.mapper.user;

import com.sdc.telegram.domain.dto.tdlib.user.status.EmojiStatusDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper
public abstract class EmojiStatusMapper {
    public abstract EmojiStatusDto toDto(final TdApi.EmojiStatus source);
}
