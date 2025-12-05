package com.sdc.telegram.domain.mapper.user;

import com.sdc.telegram.domain.dto.tdlib.user.ProfilePhotoDto;
import com.sdc.telegram.domain.mapper.message.content.FileMapper;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper(uses = FileMapper.class)
public abstract class ProfilePhotoMapper {
    public abstract ProfilePhotoDto toDto(final TdApi.ProfilePhoto source);
}
