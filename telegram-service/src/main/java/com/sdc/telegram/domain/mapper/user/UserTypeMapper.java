package com.sdc.telegram.domain.mapper.user;

import com.sdc.telegram.domain.dto.tdlib.user.UserTypeBotDto;
import com.sdc.telegram.domain.dto.tdlib.user.UserTypeDeletedDto;
import com.sdc.telegram.domain.dto.tdlib.user.UserTypeRegularDto;
import com.sdc.telegram.domain.dto.tdlib.user.UserTypeTdlib;
import com.sdc.telegram.domain.dto.tdlib.user.UserTypeUnknownDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper
public abstract class UserTypeMapper {
    public UserTypeTdlib toDto(final TdApi.UserType source){
        if (source instanceof TdApi.UserTypeDeleted){
            return toUserTypeDeletedDto((TdApi.UserTypeDeleted) source);
        } else if (source instanceof TdApi.UserTypeRegular) {
            return toUserTyRegularDto((TdApi.UserTypeRegular) source);
        } else if (source instanceof TdApi.UserTypeUnknown){
            return toUserTypeUnknownDto((TdApi.UserTypeUnknown) source);
        } else if (source instanceof TdApi.UserTypeBot){
            return toUserTypeBotDto((TdApi.UserTypeBot) source);
        } else {
            return null;
        }
    }

    public abstract UserTypeDeletedDto toUserTypeDeletedDto(final TdApi.UserTypeDeleted source);

    public abstract UserTypeRegularDto toUserTyRegularDto(final TdApi.UserTypeRegular source);

    public abstract UserTypeUnknownDto toUserTypeUnknownDto(final TdApi.UserTypeUnknown source);

    public abstract UserTypeBotDto toUserTypeBotDto(final TdApi.UserTypeBot source);
}
