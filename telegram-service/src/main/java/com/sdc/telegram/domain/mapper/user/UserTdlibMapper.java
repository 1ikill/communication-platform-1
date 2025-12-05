package com.sdc.telegram.domain.mapper.user;

import com.sdc.telegram.domain.dto.tdlib.user.UserTdlibDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {UserTypeMapper.class, UserStatusMapper.class, EmojiStatusMapper.class, ProfilePhotoMapper.class,
        UsernamesMapper.class, VerificationStatusMapper.class})
public abstract class UserTdlibMapper {
    @Mapping(target = "accountId", source = "accountId")
    public abstract UserTdlibDto toDto(final TdApi.User source, final String accountId);
}
