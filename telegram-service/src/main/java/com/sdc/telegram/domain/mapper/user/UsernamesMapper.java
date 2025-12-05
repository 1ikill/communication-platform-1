package com.sdc.telegram.domain.mapper.user;

import com.sdc.telegram.domain.dto.tdlib.user.UsernamesDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper
public abstract class UsernamesMapper {
    public abstract UsernamesDto toDto(final TdApi.Usernames source);
}
