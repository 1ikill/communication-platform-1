package com.sdc.telegram.domain.mapper.user;

import com.sdc.telegram.domain.dto.tdlib.user.status.UserStatusEmptyDto;
import com.sdc.telegram.domain.dto.tdlib.user.status.UserStatusLastMonthDto;
import com.sdc.telegram.domain.dto.tdlib.user.status.UserStatusLastWeekDto;
import com.sdc.telegram.domain.dto.tdlib.user.status.UserStatusOfflineDto;
import com.sdc.telegram.domain.dto.tdlib.user.status.UserStatusOnlineDto;
import com.sdc.telegram.domain.dto.tdlib.user.status.UserStatusRecentlyDto;
import com.sdc.telegram.domain.dto.tdlib.user.status.UserStatusTdlib;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper
public abstract class UserStatusMapper {
    public UserStatusTdlib toDto(final TdApi.UserStatus source){
        if (source instanceof TdApi.UserStatusLastWeek){
            return toUserStatusLastWeekDto((TdApi.UserStatusLastWeek) source);
        } else if (source instanceof TdApi.UserStatusLastMonth) {
            return toUserStatusLastMonthDto((TdApi.UserStatusLastMonth) source);
        } else if (source instanceof TdApi.UserStatusRecently) {
            return toUserStatusRecentlyDto((TdApi.UserStatusRecently) source);
        } else if (source instanceof TdApi.UserStatusOffline) {
            return toUserStatusOfflineDto((TdApi.UserStatusOffline) source);
        } else if (source instanceof TdApi.UserStatusOnline){
            return toUserStatusOnlineDto((TdApi.UserStatusOnline) source);
        } else if (source instanceof TdApi.UserStatusEmpty) {
            return toUserStatusEmptyDto((TdApi.UserStatusEmpty) source);
        } else {
            return null;
        }
    }

    public abstract UserStatusLastWeekDto toUserStatusLastWeekDto(final TdApi.UserStatusLastWeek source);

    public abstract UserStatusLastMonthDto toUserStatusLastMonthDto(final TdApi.UserStatusLastMonth source);

    public abstract UserStatusRecentlyDto toUserStatusRecentlyDto(final TdApi.UserStatusRecently source);

    public abstract UserStatusOfflineDto toUserStatusOfflineDto(final TdApi.UserStatusOffline source);

    public abstract UserStatusOnlineDto toUserStatusOnlineDto(final TdApi.UserStatusOnline source);

    public abstract UserStatusEmptyDto toUserStatusEmptyDto(final TdApi.UserStatusEmpty source);
}
