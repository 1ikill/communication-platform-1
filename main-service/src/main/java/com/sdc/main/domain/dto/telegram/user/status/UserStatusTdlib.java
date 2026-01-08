package com.sdc.main.domain.dto.telegram.user.status;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.main.domain.constants.telegram.TelegramUserStatusType;
import lombok.Data;

/**
 * Абстрактнйы класс статуса пользователя
 * @since 01.2025
 */
@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserStatusEmptyDto.class, name = TelegramUserStatusType.EMPTY_NAME),
        @JsonSubTypes.Type(value = UserStatusLastMonthDto.class, name = TelegramUserStatusType.LAST_MONTH_NAME),
        @JsonSubTypes.Type(value = UserStatusLastWeekDto.class, name = TelegramUserStatusType.LAST_WEEK_NAME),
        @JsonSubTypes.Type(value = UserStatusRecentlyDto.class, name = TelegramUserStatusType.RECENTLY_NAME),
        @JsonSubTypes.Type(value = UserStatusOfflineDto.class, name = TelegramUserStatusType.OFFLINE_NAME),
        @JsonSubTypes.Type(value = UserStatusOnlineDto.class, name = TelegramUserStatusType.ONLINE_NAME)
})
public abstract class UserStatusTdlib {
}
