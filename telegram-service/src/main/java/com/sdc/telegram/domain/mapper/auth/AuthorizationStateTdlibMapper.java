package com.sdc.telegram.domain.mapper.auth;

import com.sdc.telegram.domain.dto.tdlib.auth.AuthorizationStateReadyDto;
import com.sdc.telegram.domain.dto.tdlib.auth.AuthorizationStateTdlib;
import com.sdc.telegram.domain.dto.tdlib.auth.AuthorizationStateWaitCodeDto;
import com.sdc.telegram.domain.dto.tdlib.auth.AuthorizationStateWaitPasswordDto;
import com.sdc.telegram.domain.dto.tdlib.auth.AuthorizationStateWaitPhoneNumberDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper(uses = AuthenticationCodeInfoDtoMapper.class)
public abstract class AuthorizationStateTdlibMapper {
    public AuthorizationStateTdlib toDto(final TdApi.AuthorizationState source) {
        if (source instanceof TdApi.AuthorizationStateReady) {
            return toAuthorizationStateReadyDto((TdApi.AuthorizationStateReady) source);
        } else if (source instanceof TdApi.AuthorizationStateWaitCode) {
            return toAuthorizationStateWaitCodeDto((TdApi.AuthorizationStateWaitCode) source);
        } else if (source instanceof TdApi.AuthorizationStateWaitPhoneNumber) {
            return toAuthorizationStateWaitPhoneNumberDto((TdApi.AuthorizationStateWaitPhoneNumber) source);
        }
        else if (source instanceof TdApi.AuthorizationStateWaitPassword) {
            return toAuthorizationStateWaitPasswordDto((TdApi.AuthorizationStateWaitPassword) source);
        }
        return null;
    }

    public abstract AuthorizationStateReadyDto toAuthorizationStateReadyDto(final TdApi.AuthorizationStateReady source);

    public abstract AuthorizationStateWaitPhoneNumberDto toAuthorizationStateWaitPhoneNumberDto(final TdApi.AuthorizationStateWaitPhoneNumber source);

    public abstract AuthorizationStateWaitCodeDto toAuthorizationStateWaitCodeDto(final TdApi.AuthorizationStateWaitCode source);

    public abstract AuthorizationStateWaitPasswordDto toAuthorizationStateWaitPasswordDto(final TdApi.AuthorizationStateWaitPassword source);
}
