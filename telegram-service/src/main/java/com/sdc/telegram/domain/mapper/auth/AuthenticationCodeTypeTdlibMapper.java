package com.sdc.telegram.domain.mapper.auth;

import com.sdc.telegram.domain.dto.tdlib.auth.AuthenticationCodeTypeSmsDto;
import com.sdc.telegram.domain.dto.tdlib.auth.AuthenticationCodeTypeTdlib;
import com.sdc.telegram.domain.dto.tdlib.auth.AuthenticationCodeTypeTelegramMessageDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper
public abstract class AuthenticationCodeTypeTdlibMapper {
    public AuthenticationCodeTypeTdlib toDto(final TdApi.AuthenticationCodeType source) {
        if (source instanceof TdApi.AuthenticationCodeTypeSms) {
            return toAuthenticationCodeTypeSmsDto((TdApi.AuthenticationCodeTypeSms) source);
        } else if (source instanceof TdApi.AuthenticationCodeTypeTelegramMessage) {
            return toAuthenticationCodeTypeTelegramMessageDto((TdApi.AuthenticationCodeTypeTelegramMessage) source);
        }
        return null;
    }

    public abstract AuthenticationCodeTypeSmsDto toAuthenticationCodeTypeSmsDto(final TdApi.AuthenticationCodeTypeSms source);

    public abstract AuthenticationCodeTypeTelegramMessageDto toAuthenticationCodeTypeTelegramMessageDto(final TdApi.AuthenticationCodeTypeTelegramMessage source);
}
