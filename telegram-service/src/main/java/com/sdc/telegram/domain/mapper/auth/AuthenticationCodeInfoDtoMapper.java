package com.sdc.telegram.domain.mapper.auth;

import com.sdc.telegram.domain.dto.tdlib.auth.AuthenticationCodeInfoDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper(uses = AuthenticationCodeTypeTdlibMapper.class)
public abstract class AuthenticationCodeInfoDtoMapper {
    public abstract AuthenticationCodeInfoDto toDto(final TdApi.AuthenticationCodeInfo source);
}
